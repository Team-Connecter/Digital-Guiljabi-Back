package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.user.ImgUrlRequest;
import com.connecter.digitalguiljabiback.dto.user.InfoRequest;
import com.connecter.digitalguiljabiback.dto.user.NicknameRequest;
import com.connecter.digitalguiljabiback.dto.user.response.AllUserResponse;
import com.connecter.digitalguiljabiback.dto.user.response.UsersInfoResponse;
import com.connecter.digitalguiljabiback.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "UserController", description = "User관련한 컨트롤러 입니다")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class UserController {
  private final UserService userService;

  @GetMapping("/users/info/my")
  public ResponseEntity<UsersInfoResponse> getUserInfo(@AuthenticationPrincipal Users user) {
    UsersInfoResponse userInfo = userService.getUserInfo(user);

    return ResponseEntity.ok(userInfo);
  }

  @PostMapping("/users/info/nickname")
  public ResponseEntity changeUserNickname(@AuthenticationPrincipal Users user, @RequestBody NicknameRequest request) {
    userService.changeUserNickname(user, request);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/users/info/profile")
  public ResponseEntity changeProfileImg(@AuthenticationPrincipal Users user, @RequestBody ImgUrlRequest request) {
    userService.changeProfileImg(user, request);

    return ResponseEntity.ok().build();
  }

  @PatchMapping("/users/info")
  public ResponseEntity changeInfo(@AuthenticationPrincipal Users user, @RequestBody InfoRequest reqeust) {
    userService.changeInfo(user, reqeust);

    return ResponseEntity.ok().build();
  }

  //adminㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
  @GetMapping("/admin/users/all")
  public ResponseEntity<AllUserResponse> getAllUser(
    @RequestParam(required = false, defaultValue = "10") int pageSize,
    @RequestParam(required = false, defaultValue = "1") int page
  ) {
    AllUserResponse all = userService.getAll(pageSize, page);

    return ResponseEntity.ok(all);
  }





}
