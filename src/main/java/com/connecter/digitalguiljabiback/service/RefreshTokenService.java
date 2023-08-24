package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.OauthType;
import com.connecter.digitalguiljabiback.domain.RefreshToken;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.login.LoginResponse;
import com.connecter.digitalguiljabiback.dto.login.UserRequest;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.exception.TokenInValidException;
import com.connecter.digitalguiljabiback.exception.UsernameDuplicatedException;
import com.connecter.digitalguiljabiback.repository.RefreshTokenRepository;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import com.connecter.digitalguiljabiback.util.Helper;
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
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  public LoginResponse reissue(String bearerRefreshToken, HttpServletRequest request) {
    if (bearerRefreshToken == null || !bearerRefreshToken.startsWith("Bearer ")) {
      throw new TokenInValidException("토큰 값이 이상함");
    }
    
    String token = bearerRefreshToken.substring(7);
    String uid = jwtService.extractUsername(token);

    Users user = userRepository.findByUid(uid)
      .orElseGet(() -> null);

    //if (user != null) {
    List<RefreshToken> refreshTokenList = refreshTokenRepository.findByUser(user);

    RefreshToken refreshToken = null;

    for (RefreshToken r: refreshTokenList) {
      if (r.getRefreshToken().equals(token)) {
        refreshToken = r;
        break;
      }
    }

    if (refreshToken == null)
      throw new TokenInValidException("리프레쉬 토큰이 존재하지 않음");

    if (jwtService.isTokenExpired(token)) {
      refreshTokenRepository.delete(refreshToken);
      throw new TokenInValidException("리프레쉬 토큰이 만료됨");
    }

    if (!refreshToken.getIp().equals(Helper.getClientIp(request))) {
      //@TODO 다른 ip에서 로그인되었다는 알림을 보냄
    }

    String accessToken = jwtService.generateAccessToken(user);
    refreshTokenRepository.delete(refreshToken);
    String refreshTokenString = jwtService.generateRefreshToken(user);

    refreshTokenRepository.save(RefreshToken.makeRefreshToken(refreshTokenString, Helper.getClientIp(request), user, jwtService.extractExpiration(refreshTokenString)));

    return LoginResponse.makeResponse(accessToken, refreshTokenString);

  }
}



