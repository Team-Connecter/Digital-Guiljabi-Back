package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.dto.user.LoginRequest;
import com.connecter.digitalguiljabiback.dto.user.LoginResponse;
import com.connecter.digitalguiljabiback.dto.user.RegisterRequest;
import com.connecter.digitalguiljabiback.exception.UsernameDuplicatedException;
import com.connecter.digitalguiljabiback.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "UserController", description = "User관련한 컨트롤러 입니다")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {
  private final UserService userService;

  @PostMapping("/") //회원가입
  public ResponseEntity register(@RequestBody RegisterRequest request) throws UsernameDuplicatedException {
    userService.register(request);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/login") //로그인
  public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(userService.authenticate(request));
  }

}
