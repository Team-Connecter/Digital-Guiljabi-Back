package com.connecter.digitalguiljabiback.security.oauth.naver;

import com.connecter.digitalguiljabiback.dto.login.AuthRequest;
import com.connecter.digitalguiljabiback.dto.login.NaverUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 네이버와의 통신을 담당하는 클라이언트 클래스
 * NaverAuthClient를 사용하여 액세스 토큰을 요청하고 사용자 정보를 가져옵니다.
 * NaverAuthUrlBuilder를 사용하여 네이버 인증 URL을 생성
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NaverClient {

    private final NaverAuthClient authClient;
    private final NaverAuthUrlBuilder authUrlBuilder;


     // 네이버 인증 URL을 반환하는 메서드
    public String getAuthUrl() {
        String authUrl = authUrlBuilder.buildAuthUrl();
        log.info("네이버 로그인 주소 = {}", authUrl);

        return authUrl;
    }

    /**
     * 네이버 콜백으로부터 받은 인증 코드를 사용하여 로그인 처리를 합니다.
     * @param req AuthRequest 객체로부터 인증 정보를 받아옵니다.
     * @return 로그인 완료 메시지
     */
    public NaverUserResponse handleCallback(AuthRequest req) {
        String accessToken = authClient.requestAccessToken(req);

        NaverUserResponse userInfo = authClient.requestUserInfo(accessToken);
        log.info("네이버 액세스 토근 발급 성공: {}", accessToken);

        return userInfo;
    }
}
