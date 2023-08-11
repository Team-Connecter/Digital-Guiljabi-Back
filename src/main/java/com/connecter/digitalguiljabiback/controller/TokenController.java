package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.RefreshToken;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.login.LoginResponse;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.repository.RefreshTokenRepository;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import com.connecter.digitalguiljabiback.service.JwtService;
import com.connecter.digitalguiljabiback.service.LoginService;
import com.connecter.digitalguiljabiback.service.RefreshTokenService;
import com.connecter.digitalguiljabiback.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "JWT 토큰", description = "토큰 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class TokenController {

  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final RefreshTokenService refreshTokenService;
  private final RefreshTokenRepository refreshTokenRepository;

  @Operation(summary = "토큰 유효성 검사", description = """
    항상 200을 반환<br>
    token을 <Authorization 헤더>에 넣어 보내면 알아서 유효성을 검사해 true, false를 보냄<br>
    swagger에 Authorization 입력값은 무시 부탁드립니당
    """)
  @GetMapping("/token/validate")
  public ResponseEntity validateJwtToken(@RequestHeader(value="Authorization", required = false) String authHeader) {
    boolean isTokenValid = false;

    if (authHeader != null) {
      String token = authHeader.substring(7);
      String uid = jwtService.extractUsername(token);

      Users user = userRepository.findByUid(uid)
        .orElseGet(() -> null);

      if (user != null && jwtService.isTokenValid(token, user))
        isTokenValid = true;
    }

    Map<String, Boolean> response = new HashMap<>();
    response.put("isTokenValid", isTokenValid);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "엑세스 토큰 재발급(리프레쉬 토큰 이용)", description = """
    refresh token을 <X-REFRESH-TOKEN 헤더>에 넣어 보내면 새로운 access token을 반환<br>
    ★★★주의★★★[Bearer asdf~~ ] 이런식으로 꼭!!! 넣어줘야함. Bearer 필수<br>
    401: 리프레쉬 토큰이 유효하지 않음<br>
    """)
  @GetMapping("/token/reissue")
  public ResponseEntity reissueAccessToken(@RequestHeader(value = "X-REFRESH-TOKEN") String bearerRefreshToken, HttpServletRequest request) {

    LoginResponse response = refreshTokenService.reissue(bearerRefreshToken, request);

    return ResponseEntity.ok(response);
  }
}
