package com.connecter.digitalguiljabiback.security.oauth.naver;

import com.connecter.digitalguiljabiback.config.properties.NaverProperties;
import com.connecter.digitalguiljabiback.security.oauth.AuthUrlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

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
public class NaverAuthUrlBuilder implements AuthUrlBuilder {

    private final NaverProperties naverProperties;

    /**
     * 카카오 인증 URL을 생성합니다.
     * @return 생성된 카카오 인증 URL 문자열
     */
    @Override
    public String buildAuthUrl() {
        String authorizationUri = naverProperties.getAuthorizationUri();
        String state = new BigInteger(130, new SecureRandom()).toString();
        String clientId = encodeQueryParam(naverProperties.getClientId());
        String redirectUri = encodeQueryParam(naverProperties.getRedirectUri());
        String response_type = "code";

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

