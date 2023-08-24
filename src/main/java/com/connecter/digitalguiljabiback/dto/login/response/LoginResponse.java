package com.connecter.digitalguiljabiback.dto.login.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
        //프론트가 통신중이니까 이전 값 유지..
        private String token;
        private String accessToken;
        private String refreshToken;

        public static LoginResponse makeResponse(String accessToken, String refreshToken) {
                return new LoginResponse(accessToken, accessToken, refreshToken);
        }
}
