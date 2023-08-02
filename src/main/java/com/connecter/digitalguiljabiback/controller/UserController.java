package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.security.oauth.kakao.KakaoClient;
import com.connecter.digitalguiljabiback.service.LoginService;
import com.connecter.digitalguiljabiback.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Tag(name = "UserController", description = "User관련한 컨트롤러 입니다")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {
  private final UserService userService;
  private final KakaoClient kakaoClient;
  private final LoginService loginService;

  @GetMapping
  public ResponseEntity getUserInfo(@AuthenticationPrincipal Users user) {
    log.info("userDetails.getUsername() = {}", user.getUsername());
    log.info("userDetails.getAuthorities() = {}", user.getAuthorities());

    return ResponseEntity.ok().build();

  }

}
