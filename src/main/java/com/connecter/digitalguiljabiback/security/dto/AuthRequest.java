package com.connecter.digitalguiljabiback.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * KakaoAuthRequest
 * 카카오 인증 서버로 보내는 요청 객체
 * 인가 코드(authorizationCode)를 이용하여 API 요청을 생성
 *
 * 작성자: hyunjin
 * 버전: 1.0.0
 * 작성일: 2023-07-29
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String authorizationCode;
    private String state;

    /**
     * API 요청 바디를 생성하는 메서드
     *
     * @return API 요청 바디
     */
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("state", state);
        return body;
    }

    public AuthRequest(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }
}


