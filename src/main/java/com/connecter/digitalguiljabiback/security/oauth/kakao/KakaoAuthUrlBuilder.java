package com.connecter.digitalguiljabiback.security.oauth.kakao;

import com.connecter.digitalguiljabiback.config.properties.KakaoProperties;
import com.connecter.digitalguiljabiback.security.oauth.AuthUrlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthUrlBuilder implements AuthUrlBuilder {

    private final KakaoProperties kakaoProperties;

    //카카오 로그인 URL 생성
    @Override
    public String buildAuthUrl() {
        String authorizationUri = kakaoProperties.getAuthorizationUri();
        String clientId = encodeQueryParam(kakaoProperties.getClientId());

        return String.format(
                "%s?client_id=%s",
                authorizationUri, clientId);
    }

    //URL 파라미터 값을 만듦
    private String encodeQueryParam(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }
}

