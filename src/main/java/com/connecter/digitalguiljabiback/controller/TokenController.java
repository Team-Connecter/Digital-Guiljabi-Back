package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import com.connecter.digitalguiljabiback.service.JwtService;
import com.connecter.digitalguiljabiback.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "JWT 토큰", description = "JWT 토큰이 유효한지 확인하는 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class TokenController {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Operation(summary = "토큰 유효성 검사", description = """
    항상 200을 반환<br>
    token을 <Authorization 헤더>에 넣어 보내면 알아서 유효성을 검사해 true, false를 보냄
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
}
