package com.connecter.digitalguiljabiback.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AuthResponse
 * 카카오, 네이버 인증 서버에서  받은  응답 객체
 * Access Token을 포함한 응답 정보를 담고 있다.
 */
@Getter
@NoArgsConstructor
public class AuthResponse {
    @JsonProperty("access_token")
    private String accessToken;
}
