package com.connecter.digitalguiljabiback.security.oauth.kakao;

import com.connecter.digitalguiljabiback.dto.login.AuthRequest;
import com.connecter.digitalguiljabiback.dto.login.response.KakaoUserResponse;
import com.connecter.digitalguiljabiback.security.oauth.Oauth2Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoClient implements Oauth2Client {

    private final KakaoAuthClient authClient;
    private final KakaoAuthUrlBuilder authUrlBuilder;

    @Override
    public String getAuthUrl() {
        String authUrl = authUrlBuilder.buildAuthUrl();
        log.info("카카오 로그인 주소 = {}", authUrl);

        return authUrl;
    }

    public KakaoUserResponse getUserInfo(AuthRequest req, String redirectUrl) {
        //엑세스토큰 발급
        String accessToken = authClient.requestAccessToken(req, redirectUrl);

        //user정보 얻기
        KakaoUserResponse userInfo = authClient.requestUserInfo(accessToken);

        log.info("카카오 액세스 토근 발급 성공: {}", accessToken);

        return userInfo;
    }

    public String getLogoutUrl() {
        String logoutUrl = authClient.getLogoutUrl();
        return logoutUrl;
    }
}
