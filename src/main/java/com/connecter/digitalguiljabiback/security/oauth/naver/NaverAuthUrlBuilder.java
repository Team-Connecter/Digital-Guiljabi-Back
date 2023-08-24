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


@Component
@RequiredArgsConstructor
@Slf4j
public class NaverAuthUrlBuilder implements AuthUrlBuilder {

    private final NaverProperties naverProperties;

    //네이버 로그인 URL 생성
    @Override
    public String buildAuthUrl() {
        String authorizationUri = naverProperties.getAuthorizationUri();
        String clientId = encodeQueryParam(naverProperties.getClientId());

        return String.format(
                "%s?client_id=%s",
                authorizationUri, clientId);
    }

    //URL 파라미터 값을 만듦
    private String encodeQueryParam(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }
}

