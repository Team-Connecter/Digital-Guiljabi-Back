package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.OauthType;
import com.connecter.digitalguiljabiback.dto.login.LoginResponse;
import com.connecter.digitalguiljabiback.dto.login.AuthRequest;
import com.connecter.digitalguiljabiback.dto.login.KakaoUserResponse;
import com.connecter.digitalguiljabiback.dto.login.NaverUserResponse;
import com.connecter.digitalguiljabiback.security.oauth.kakao.KakaoClient;
import com.connecter.digitalguiljabiback.security.oauth.naver.NaverClient;
import com.connecter.digitalguiljabiback.service.LoginService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
@Slf4j
public class LoginController {

  private final KakaoClient kakaoClient;
  private final NaverClient naverClient;
  private final LoginService loginService;

   //카카오 로그인 페이지 url을 반환
  @GetMapping("/kakao")
  public ResponseEntity<Map> getKakaoLoginUrl() {
    Map<String, String> map = new HashMap<>();
    map.put("loginUrl", kakaoClient.getAuthUrl());
    return ResponseEntity.status(HttpStatus.CREATED).body(map);
  }

  /**
   * 카카오 로그인 콜백을 처리하고 인증에 성공한 경우 로그인 또는 회원 가입
   * @param authorizationCode 인가 코드
   * @return AuthResponseDTO 로그인 또는 회원 가입 결과를 담은 응답 DTO
   */
  @Hidden
  @GetMapping("/callback")
  public ResponseEntity<LoginResponse> processKakaoLoginCallback(@RequestParam("code") String authorizationCode) {
    AuthRequest params = new AuthRequest(authorizationCode);
    KakaoUserResponse response = kakaoClient.handleCallback(params);

    log.info("사용자 UID: {}", response.getUid());

    LoginResponse loginResponseDTO = loginService.loginOrCreate(response.toUserRequest(), OauthType.KAKAO);

    return ResponseEntity.ok(loginResponseDTO);
  }

  //네이버 로그인 페이지 url을 반환
  @GetMapping("/naver")
  public ResponseEntity<Map> getNaverLoginUrl() {
    Map<String, String> map = new HashMap<>();
    map.put("loginUrl", naverClient.getAuthUrl());
    return ResponseEntity.status(HttpStatus.CREATED).body(map);
  }

  /**
   * 네이버 로그인 콜백을 처리하고 인증에 성공한 경우 로그인 또는 회원 가입
   * @param authorizationCode 인가 코드
   * @param state 사이트 간 요청 위조 공격을 방지하기 위해 애플리케이션에서 생성한 상태 토큰값으로 URL 인코딩을 적용한 값을 사용
   * @return AuthResponseDTO 로그인 또는 회원 가입 결과를 담은 응답 DTO
   */
  @Hidden
  @GetMapping("/callback/naver")
  public ResponseEntity<LoginResponse> processNaverLoginCallback(
    @RequestParam("code") String authorizationCode,
    @RequestParam("state") String state
  ) {
    AuthRequest params = new AuthRequest(authorizationCode, state);
    NaverUserResponse response = naverClient.handleCallback(params);

    log.info("사용자 UID: {}", response.getId());

    LoginResponse loginResponseDTO = loginService.loginOrCreate(response.toUserRequest(), OauthType.NAVER);

    return ResponseEntity.ok(loginResponseDTO);
  }
}
