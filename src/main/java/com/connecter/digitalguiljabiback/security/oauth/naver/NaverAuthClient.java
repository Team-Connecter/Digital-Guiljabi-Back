package com.connecter.digitalguiljabiback.security.oauth.naver;

import com.connecter.digitalguiljabiback.config.properties.NaverProperties;
import com.connecter.digitalguiljabiback.exception.KakaoClientException;
import com.connecter.digitalguiljabiback.exception.NaverClientException;
import com.connecter.digitalguiljabiback.dto.login.AuthRequest;
import com.connecter.digitalguiljabiback.dto.login.response.AuthResponse;
import com.connecter.digitalguiljabiback.dto.login.response.NaverUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverAuthClient {

    private final NaverProperties naverProperties;
    private final RestTemplate restTemplate;


    public String requestAccessToken(AuthRequest request) throws KakaoClientException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(naverProperties.getTokenUri())
          .queryParam("grant_type", naverProperties.getGrantType())
          .queryParam("client_id", naverProperties.getClientId())
          .queryParam("client_secret", naverProperties.getClientSecret())
          .queryParam("code", request.getAuthorizationCode())
          .queryParam("state", URLEncoder.encode(request.getState(), StandardCharsets.UTF_8))
          .build(true);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        try {
            ResponseEntity<AuthResponse> respEntity = restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, AuthResponse.class);

            AuthResponse response = respEntity.getBody();

            if (response == null || response.getAccessToken() == null) {
                throw new KakaoClientException("네이버 API에서 액세스 토큰을 가져오지 못했습니다.");
            }
            return response.getAccessToken();
        } catch (RestClientException e) {
            throw new KakaoClientException("네이버 API에서 액세스 토큰 요청 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public NaverUserResponse requestUserInfo(String accessToken) throws NaverClientException {
        NaverUserResponse naverUserResponse;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

        try {
            naverUserResponse = restTemplate.postForObject(naverProperties.getUserInfoUri(), httpEntity, NaverUserResponse.class);
        } catch (RestClientException e) {
            throw new NaverClientException("네이버 API에서 사용자 정보 요청 중 예외가 발생했습니다: " + e.getMessage(), e);
        }

        return naverUserResponse;
    }
}

