package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.OauthType;
import com.connecter.digitalguiljabiback.domain.RefreshToken;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.login.LoginResponse;
import com.connecter.digitalguiljabiback.dto.login.UserRequest;
import com.connecter.digitalguiljabiback.exception.UserLockedException;
import com.connecter.digitalguiljabiback.exception.UsernameDuplicatedException;
import com.connecter.digitalguiljabiback.repository.RefreshTokenRepository;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 사용자 서비스 클래스
 * 다양한 사용자 어댑터들을 활용하여 로그인 또는 회원 가입을 처리합니다.
 * 사용자 어댑터들은 UserDTO 객체를 받아서 해당 사용자에 맞게 로그인 또는 회원 가입을 수행합니다.
 * 이 클래스는 UserDTO 객체를 사용하여 적절한 사용자 어댑터를 선택하고,
 * 해당 어댑터를 통해 로그인 또는 회원 가입 처리를 수행합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoginService {
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  /**
   * 로그인 또는 회원 가입 처리를 수행
   * @param userDTO 로그인 또는 회원 가입에 필요한 사용자 정보 객체
   * @param oauthType oauth 로그인 시 타입지정: KAKAO, NAVER 등
   * @return 인증 응답 DTO
   */
  public LoginResponse loginOrCreate(UserRequest userDTO, OauthType oauthType, HttpServletRequest request) {
    Users user = userRepository.findByUid(oauthType.name() + userDTO.getUid())
      .orElseGet(() -> null);

    if (user == null) {
      //회원가입
      Users newUser = Users.makeUsers(oauthType.name() + userDTO.getUid(), passwordEncoder.encode(userDTO.getUid()), oauthType);
      user = userRepository.save(newUser);
    }

    if (!user.isAccountNonExpired())
      throw new UserLockedException();

    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        user.getUsername(),
        userDTO.getUid()
      )
    );

    //엑세스 토큰 생성
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    refreshTokenRepository.save(
      RefreshToken.makeRefreshToken(refreshToken, getClientIp(request), user, jwtService.extractExpiration(refreshToken))
    );

    return LoginResponse.makeResponse(accessToken, refreshToken);
  }

  public static String getClientIp(HttpServletRequest request) {
    String clientIp = null;
    boolean isIpInHeader = false;

    List<String> headerList = new ArrayList<>();
    headerList.add("X-Forwarded-For");
    headerList.add("HTTP_CLIENT_IP");
    headerList.add("HTTP_X_FORWARDED_FOR");
    headerList.add("HTTP_X_FORWARDED");
    headerList.add("HTTP_FORWARDED_FOR");
    headerList.add("HTTP_FORWARDED");
    headerList.add("Proxy-Client-IP");
    headerList.add("WL-Proxy-Client-IP");
    headerList.add("HTTP_VIA");
    headerList.add("IPV6_ADR");

    for (String header : headerList) {
      clientIp = request.getHeader(header);
      if (StringUtils.hasText(clientIp) && !clientIp.equals("unknown")) {
        isIpInHeader = true;
        break;
      }
    }

    if (!isIpInHeader) {
      clientIp = request.getRemoteAddr();
    }

    return clientIp;
  }

  public void tempSignUp(UserRequest userDto) throws UsernameDuplicatedException {
    Users user = userRepository.findByUid(userDto.getUid())
      .orElseGet(() -> null);

    if (user != null)
      throw new UsernameDuplicatedException("해당하는 uid가 존재합니다");

    Users newUser = Users.makeUsers(userDto.getUid(), passwordEncoder.encode(userDto.getUid()), null);
    userRepository.save(newUser);
  }

  public LoginResponse tempLogin(UserRequest userDto, HttpServletRequest request) {
    Users user = userRepository.findByUid(userDto.getUid())
      .orElseGet(() -> null);

    if (!user.isAccountNonLocked()) {
      throw new UserLockedException();
    }

    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        userDto.getUid(),
        userDto.getUid()
      )
    );

    //토큰 생성
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    refreshTokenRepository.save(
      RefreshToken.makeRefreshToken(refreshToken, getClientIp(request), user, jwtService.extractExpiration(refreshToken))
    );

    return LoginResponse.makeResponse(accessToken, refreshToken);
  }
}



