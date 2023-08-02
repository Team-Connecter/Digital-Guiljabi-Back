package com.connecter.digitalguiljabiback.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * AuthRequest
 * 카카오, 네이버 인증 서버로 보내는 요청 객체
 * 인가 코드(authorizationCode)를 이용하여 API 요청을 생성
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String authorizationCode;
    private String state; //네이버 로그인만 사용하는 값

    /**
     * API 요청 바디를 생성하는 메서드
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


