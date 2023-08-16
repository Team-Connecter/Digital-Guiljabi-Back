package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.OauthType;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.login.*;
import com.connecter.digitalguiljabiback.exception.UsernameDuplicatedException;
import com.connecter.digitalguiljabiback.security.oauth.kakao.KakaoClient;
import com.connecter.digitalguiljabiback.security.oauth.naver.NaverClient;
import com.connecter.digitalguiljabiback.service.JwtService;
import com.connecter.digitalguiljabiback.service.LoginService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "로그인", description = "로그인 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class LoginController {

  private final KakaoClient kakaoClient;
  private final NaverClient naverClient;
  private final LoginService loginService;
  private final JwtService jwtService;

  @Operation(summary = "카카오 로그인 페이지 url 받기", description = """
    [모두 접근가능] 카카오로 로그인할 수 있는 url을 받아옵니다.<br>
    회원가입이 끝나면 꼭 유저닉네임을 설정하도록 해주세요!!<br>
    201: 성공
    """)
  @GetMapping("/auth/kakao/login-url")
  public ResponseEntity<Map> getKakaoLoginUrl() {
    Map<String, String> map = new HashMap<>();
    map.put("loginUrl", kakaoClient.getAuthUrl());
    return ResponseEntity.status(HttpStatus.CREATED).body(map);
  }
  @GetMapping("/login/kakao/test")
  public ResponseEntity asdf(
    @RequestParam("code") String authorizationCode
  ) {
    return ResponseEntity.ok(authorizationCode);
  }

  /**
   * 카카오 로그인 콜백을 처리하고 인증에 성공한 경우 로그인 또는 회원 가입
   * @param authorizationCode 인가 코드
   * @return AuthResponseDTO 로그인 또는 회원 가입 결과를 담은 응답 DTO
   */
  @Operation(summary = "카카오 로그인", description = """
    [모두 접근가능] 카카오 로그인 후 받은 인가코드를 넘겨주면 로그인이 가능<br>
    redirect_url은 카카오로그인 url 요청 시 사용한 값을 넣어주세요<br>
    회원가입이 끝나면 꼭 유저닉네임을 설정하도록 해주세요!!<br>
    200: 성공
    """)
  @GetMapping("/login/kakao")
  public ResponseEntity<LoginResponse> processKakaoLoginCallback(
    @RequestParam("code") String authorizationCode,
    @RequestParam("redirectUrl") String redirectUrl,
    HttpServletRequest request
  ) {
    AuthRequest params = new AuthRequest(authorizationCode.trim());
    KakaoUserResponse response = kakaoClient.handleCallback(params, redirectUrl);

    log.info("사용자 UID: {}", response.getUid());

    LoginResponse loginResponseDTO = loginService.loginOrCreate(response.toUserRequest(), OauthType.KAKAO, request);

    return ResponseEntity.ok(loginResponseDTO);
  }

  @Operation(summary = "네이버 로그인 페이지 url 받기", description = """
    [모두 접근가능] 네이버로 로그인할 수 있는 url을 받아옵니다.<br>
    회원가입이 끝나면 꼭 유저닉네임을 설정하도록 해주세요!!<br>
    https://nid.naver.com/oauth2.0/authorize?client_id={}&redirect_uri={}&response_type=code<br>
    201: 성공
    """)
  @GetMapping("/login/naver/login-url")
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

  @GetMapping("/login/callback/naver")
  public ResponseEntity processNaverLoginCallback(
    @RequestParam("code") String authorizationCode,
    @RequestParam("state") String state
  ){
    return ResponseEntity.ok(authorizationCode + "     " + state);
  }

  @Operation(summary = "네이버 로그인", description = """
    [모두 접근가능] 네이버 로그인 후 받은 인가코드를 넘겨주면 로그인이 가능<br>
    state는 사이트 간 요청 위조 공격을 방지하기 위해 생성한 값입니다. 필수로 넣어주세용<br>
    ex) BigInteger(130, new SecureRandom()).toString() <br>
    회원가입이 끝나면 꼭 유저닉네임을 설정하도록 해주세요!!<br>
    200: 성공
    """)
  @GetMapping("/login/naver")
  public ResponseEntity<LoginResponse> processNaverLoginCallback(
    @RequestParam("code") String authorizationCode,
    @RequestParam("state") String state,
    HttpServletRequest request
  ) {
    AuthRequest params = new AuthRequest(authorizationCode, state);
    NaverUserResponse response = naverClient.handleCallback(params);

    log.info("사용자 UID: {}", response.getId());

    LoginResponse loginResponseDTO = loginService.loginOrCreate(response.toUserRequest(), OauthType.NAVER, request);

    return ResponseEntity.ok(loginResponseDTO);
  }

  @Operation(summary = "임시 회원가입", description = """
    [모두 접근가능] uid로 임시 회원가입을 할 수 있습니다.<br>
    회원가입이 끝나면 꼭 유저닉네임을 설정하도록 해주세요!!<br>
    201: 성공<br>
    409: 같은 uid가 이미 존재함
    """)
  @PostMapping("/login/signup")
  public ResponseEntity tempSignUp(@RequestBody UserRequest userRequest) throws UsernameDuplicatedException {
    loginService.tempSignUp(userRequest);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "임시 로그인", description = """
    [모두 접근가능] uid로 임시 로그인을 할 수 있습니다.<br>
    200: 성공<br>
    403: 가입하지 않은 uid or 정지된 회원
    """)
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> tempLogin(@RequestBody UserRequest userRequest, HttpServletRequest request) {
    LoginResponse loginResponse = loginService.tempLogin(userRequest, request);

    return ResponseEntity.ok(loginResponse);
  }

  @Operation(summary = "로그아웃", description = """
    [로그인 필요] 로그아웃을 합니다.<br> 
    카카오 로그아웃의 경우 logout url이 반환되고, jwt토큰이 만료됩니다<br>
    네이버, 일반 로그아웃의 경우 jwt토큰만 만료됩니다<br>
    200: 성공<br>
    """)
  @PostMapping("/logout")
  public ResponseEntity<Map> logout(@AuthenticationPrincipal Users user, @RequestHeader(value="Authorization") String authHeader) {
    OauthType type = user.getOauthType();
    Map<String, String> map = new HashMap<>();

    if (type == OauthType.KAKAO)
        map.put("logoutUrl", kakaoClient.getLogoutUrl());

    String token = authHeader.substring(7);
    
    //jwt토큰만료처리 -> 블랙리스트
    jwtService.addBlackList(token);

    return ResponseEntity.ok(map);
  }
}
