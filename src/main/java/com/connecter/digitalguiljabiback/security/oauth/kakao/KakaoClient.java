package com.connecter.digitalguiljabiback.security.oauth.kakao;

import com.connecter.digitalguiljabiback.security.dto.AuthRequest;
import com.connecter.digitalguiljabiback.security.dto.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * KakaoClient
 * 카카오와의 통신을 담당하는 클라이언트 클래스입니다.
 * KakaoAuthClient를 사용하여 액세스 토큰을 요청하고 사용자 정보를 가져옵니다.
 * KakaoAuthUrlBuilder를 사용하여 카카오 인증 URL을 생성합니다.
 * 작성자: hyunjin
 * 버전: 1.0.0
 * 작성일: 2023-07-30
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoClient {

    private final KakaoAuthClient authClient;
    private final KakaoAuthUrlBuilder authUrlBuilder;

    /**
     * 카카오 인증 URL로 리다이렉트하는 메서드입니다.
     * @return 리다이렉트 응답
     */
    public String redirectToKakaoAuth() {
        String authUrl = authUrlBuilder.buildAuthUrl();
        log.info("카카오 로그인 주소 = {}", authUrl);

        return authUrl;
    }

    /**
     * 카카오 콜백으로부터 받은 인증 코드를 사용하여 로그인 처리를 합니다.
     * @param req KakaoAuthRequest 객체로부터 인증 정보를 받아옵니다.
     * @return 로그인 완료 메시지
     */
    public KakaoUserResponse handleCallback(AuthRequest req) {
        String accessToken = authClient.requestAccessToken(req);

        KakaoUserResponse userInfo = authClient.requestUserInfo(accessToken);
        log.info("카카오 액세스 토근 발급 성공: {}", accessToken);

        return userInfo;
    }
}
