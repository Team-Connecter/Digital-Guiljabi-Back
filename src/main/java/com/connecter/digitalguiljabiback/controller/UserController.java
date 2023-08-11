package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.user.ImgUrlRequest;
import com.connecter.digitalguiljabiback.dto.user.InfoRequest;
import com.connecter.digitalguiljabiback.dto.user.NicknameRequest;
import com.connecter.digitalguiljabiback.dto.user.response.AllUserResponse;
import com.connecter.digitalguiljabiback.dto.user.response.UsersInfoResponse;
import com.connecter.digitalguiljabiback.exception.InternalServerException;
import com.connecter.digitalguiljabiback.exception.UsernameDuplicatedException;
import com.connecter.digitalguiljabiback.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@Tag(name = "사용자", description = "사용자 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class UserController {
  private final UserService userService;

  @Operation(summary = "내 정보 조회", description = """
    [로그인 필요] 최상위 카테고리를 조회합니다<br>
    200: 성공<br>
    403: 로그인 필요
    """)
  @GetMapping("/users/info/my")
  public ResponseEntity<UsersInfoResponse> getUserInfo(@AuthenticationPrincipal Users user) {
    UsersInfoResponse userInfo = userService.getUserInfo(user);

    return ResponseEntity.ok(userInfo);
  }

  @Operation(summary = "닉네임 처음 등록", description = """
    [로그인 필요] 회원가입 후 닉네임을 처음으로 등록합니다.<br>
    200: 성공<br>
    403: 로그인 필요<br>
    409: 같은 닉네임이 이미 존재함
    """)
  @PostMapping("/users/info/nickname")
  public ResponseEntity changeUserNickname(
    @AuthenticationPrincipal Users user,
    @RequestBody @Valid NicknameRequest request
  ) throws NoSuchElementException, UsernameDuplicatedException {
    userService.changeUserNickname(user, request);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "이미 존재하는 닉네임인지 확인", description = """
    [모두 접근가능] 해당 닉네임이 이미 존재하는 닉네임인지 확인합니다<br>
    200: 항상 성공
    """)
  @GetMapping("/users/nickname/{nickname}/exists")
  public ResponseEntity hasNickname(@PathVariable String nickname) {
    Map<String, Boolean> map = new HashMap<>();
    map.put("hasNickname", userService.hasNickname(nickname));

    return ResponseEntity.ok(map);
  }

  @Operation(summary = "프로필 이미지 변경", description = """
    [로그인 필요] 프로필 이미지를 변경합니다<br>
    200: 성공<br>
    403: 로그인 필요
    """)
  @PostMapping("/users/info/profile")
  public ResponseEntity changeProfileImg(
    @AuthenticationPrincipal Users user,
    @RequestBody @Valid ImgUrlRequest request
  ) throws InternalServerException {
    userService.changeProfileImg(user, request);

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "사용자 정보 한 번에 변경(자기소개, 닉네임 등)", description = """
    [로그인 필요] 사용자 정보를 변경합니다<br>
    200: 성공<br>
    403: 로그인 필요
    """)
  @PatchMapping("/users/info")
  public ResponseEntity changeInfo(@AuthenticationPrincipal Users user, @RequestBody InfoRequest reqeust) throws NoSuchElementException {
    userService.changeInfo(user, reqeust);

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "회원탈퇴", description = """
    [로그인 필요] 회원 탈퇴를 진행합니다<br>
    200: 성공
    """)
  @DeleteMapping("/users")
  public ResponseEntity deleteUser(@AuthenticationPrincipal Users user) throws InternalServerException {
    userService.delete(user);

    return ResponseEntity.ok().build();
  }

  //adminㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
  @Operation(summary = "모든 회원 조회", description = """
    [관리자] 모든 회원을 조회합니다<br>
    200: 성공
    """)
  @GetMapping("/admin/users/all")
  public ResponseEntity<AllUserResponse> getAllUser(
    @RequestParam(required = false, defaultValue = "10") int pageSize,
    @RequestParam(required = false, defaultValue = "1") int page
  ) {
    AllUserResponse all = userService.getAll(pageSize, page);

    return ResponseEntity.ok(all);
  }





}
