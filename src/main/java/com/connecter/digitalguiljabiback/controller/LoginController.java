package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.dto.user.LoginResponseDTO;
import com.connecter.digitalguiljabiback.security.dto.KakaoAuthRequest;
import com.connecter.digitalguiljabiback.security.dto.KakaoUserResponse;
import com.connecter.digitalguiljabiback.security.oauth.KakaoClient;
import com.connecter.digitalguiljabiback.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
@Slf4j
public class LoginController {

  private final KakaoClient kakaoClient;
  private final LoginService loginService;

  /**
   * 카카오 로그인 페이지로 리다이렉트
   * @return ResponseEntity<Void> 리다이렉트 응답
   */
  @GetMapping("/kakao")
  public ResponseEntity<Map> redirectToKakaoLogin() {
    Map<String, String> map = new HashMap<>();
    map.put("loginUrl", kakaoClient.redirectToKakaoAuth());
    return ResponseEntity.ok(map);
  }

  /**
   * 카카오 로그인 콜백을 처리하고 인증에 성공한 경우 로그인 또는 회원 가입
   * @param authorizationCode 인가 코드
   * @return AuthResponseDTO 로그인 또는 회원 가입 결과를 담은 응답 DTO
   */
//    @Hidden
  @GetMapping("/callback")
  public ResponseEntity<LoginResponseDTO> processKakaoLoginCallback(@RequestParam("code") String authorizationCode) {
    KakaoAuthRequest params = new KakaoAuthRequest(authorizationCode);
    KakaoUserResponse response = kakaoClient.handleCallback(params);

    log.info("사용자 UID: {}", response.getUid());

    LoginResponseDTO loginResponseDTO = loginService.loginOrCreate(response.toUserRequest());

    return ResponseEntity.ok(loginResponseDTO);
  }
}
