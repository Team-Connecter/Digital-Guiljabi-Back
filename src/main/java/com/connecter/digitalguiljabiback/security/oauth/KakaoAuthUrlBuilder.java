package com.connecter.digitalguiljabiback.security.oauth;

import com.connecter.digitalguiljabiback.config.properties.KakaoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * KakaoAuthUrlBuilder
 * 카카오 인증 URL을 생성하는 빌더 클래스입니다.
 * 작성자: hyunjin
 * 버전: 1.0.0
 * 작성일: 2023-07-30
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthUrlBuilder {

    private final KakaoProperties kakaoProperties;

    /**
     * 카카오 인증 URL을 생성합니다.
     * @return 생성된 카카오 인증 URL 문자열
     */
    public String buildKakaoAuthUrl() {
        String authorizationUri = kakaoProperties.getAuthorizationUri();
        String clientId = encodeQueryParam(kakaoProperties.getClientId());
        String redirectUri = encodeQueryParam(kakaoProperties.getRedirectUri());

        return String.format(
                "%s?client_id=%s&redirect_uri=%s&response_type=%s",
                authorizationUri, clientId, redirectUri, "code");
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

