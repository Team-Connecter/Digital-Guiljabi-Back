package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.OauthType;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.user.LoginResponseDTO;
import com.connecter.digitalguiljabiback.dto.user.UserRequest;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 사용자 서비스 클래스
 * 다양한 사용자 어댑터들을 활용하여 로그인 또는 회원 가입을 처리합니다.
 * <p>
 * 사용자 어댑터들은 UserDTO 객체를 받아서 해당 사용자에 맞게 로그인 또는 회원 가입을 수행합니다.
 * 이 클래스는 UserDTO 객체를 사용하여 적절한 사용자 어댑터를 선택하고,
 * 해당 어댑터를 통해 로그인 또는 회원 가입 처리를 수행합니다.
 * <p>
 * 작성자: hyunjin
 * 버전: 1.0.0
 * 작성일: 2023년 7월 30일
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoginService {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  /**
   * 로그인 또는 회원 가입 처리를 수행합니다.
   *
   * @param userDTO 로그인 또는 회원 가입에 필요한 사용자 정보 객체
   * @return 인증 응답 DTO
   * @throws IllegalArgumentException 지원되지 않는 사용자일 경우 예외를 던집니다.
   */
  public LoginResponseDTO loginOrCreate(UserRequest userDTO, OauthType oauthType) {
    Users user = userRepository.findByUid(oauthType.name() + userDTO.getUid())
      .orElseGet(() -> null);

    if (user == null) {
      //회원가입
      Users newUser = Users.makeUsers(oauthType.name() + userDTO.getUid(), passwordEncoder.encode(userDTO.getUid()), oauthType);
      user = userRepository.save(newUser);
    }

    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        user.getUsername(),
        userDTO.getUid()
      )
    );

    String jwtToken = jwtService.generateAccessToken(user);

    return LoginResponseDTO.builder()
      .token(jwtToken)
      .userPk(user.getPk())
      .build();
  }

}



