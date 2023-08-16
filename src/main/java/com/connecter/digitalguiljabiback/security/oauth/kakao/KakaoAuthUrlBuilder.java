package com.connecter.digitalguiljabiback.security.oauth.kakao;

import com.connecter.digitalguiljabiback.config.properties.KakaoProperties;
import com.connecter.digitalguiljabiback.security.oauth.AuthUrlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * KakaoAuthUrlBuilder
 * 카카오 인증 URL을 생성하는 빌더 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthUrlBuilder implements AuthUrlBuilder {

    private final KakaoProperties kakaoProperties;

    /**
     * 카카오 인증 URL을 생성합니다.
     * @return 생성된 카카오 인증 URL 문자열
     */
    @Override
    public String buildAuthUrl() {
        String authorizationUri = kakaoProperties.getAuthorizationUri();
        String clientId = encodeQueryParam(kakaoProperties.getClientId());

        return String.format(
                "%s?client_id=%s",
                authorizationUri, clientId);
    }

    /**
     * URL 파라미터 값을 인코딩합니다.
     * @param param 인코딩할 파라미터 문자열
     * @return 인코딩된 파라미터 문자열
     */
    private String encodeQueryParam(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }
}

