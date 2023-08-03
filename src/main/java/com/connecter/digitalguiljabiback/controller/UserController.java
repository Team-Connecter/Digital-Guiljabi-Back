package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.user.ImgUrlRequest;
import com.connecter.digitalguiljabiback.dto.user.InfoRequest;
import com.connecter.digitalguiljabiback.dto.user.NicknameRequest;
import com.connecter.digitalguiljabiback.security.oauth.kakao.KakaoClient;
import com.connecter.digitalguiljabiback.service.LoginService;
import com.connecter.digitalguiljabiback.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

  @PostMapping("/info/nickname")
  public ResponseEntity changeUserNickname(@AuthenticationPrincipal Users user, @RequestBody NicknameRequest request) {
    userService.changeUserNickname(user, request);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/info/profile")
  public ResponseEntity changeProfileImg(@AuthenticationPrincipal Users user, @RequestBody ImgUrlRequest request) {
    userService.changeProfileImg(user, request);

    return ResponseEntity.ok().build();
  }

  @PatchMapping("/info")
  public ResponseEntity changeInfo(@AuthenticationPrincipal Users user, @RequestBody InfoRequest reqeust) {
    userService.changeInfo(user, reqeust);

    return ResponseEntity.ok().build();
  }



}
