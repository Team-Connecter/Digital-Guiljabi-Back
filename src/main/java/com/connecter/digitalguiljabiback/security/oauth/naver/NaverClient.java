package com.connecter.digitalguiljabiback.security.oauth.naver;

import com.connecter.digitalguiljabiback.dto.login.AuthRequest;
import com.connecter.digitalguiljabiback.dto.login.response.NaverUserResponse;
import com.connecter.digitalguiljabiback.security.oauth.Oauth2Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverClient implements Oauth2Client {

    private final NaverAuthClient authClient;
    private final NaverAuthUrlBuilder authUrlBuilder;


     // 네이버 인증 URL을 반환하는 메서드
    public String getAuthUrl() {
        String authUrl = authUrlBuilder.buildAuthUrl();
        log.info("네이버 로그인 주소 = {}", authUrl);

        return authUrl;
    }

    @Override
    public NaverUserResponse getUserInfo(AuthRequest req, String redirectUrl) {
        return getUserInfo(req);
    }

    public NaverUserResponse getUserInfo(AuthRequest req) {
        String accessToken = authClient.requestAccessToken(req);

        NaverUserResponse userInfo = authClient.requestUserInfo(accessToken);
        log.info("네이버 액세스 토근 발급 성공: {}", accessToken);

        return userInfo;
    }


}
