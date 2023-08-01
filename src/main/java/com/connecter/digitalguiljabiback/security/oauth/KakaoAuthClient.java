package com.connecter.digitalguiljabiback.security.oauth;

import com.connecter.digitalguiljabiback.config.properties.KakaoProperties;
import com.connecter.digitalguiljabiback.exception.KakaoClientException;
import com.connecter.digitalguiljabiback.security.dto.KakaoAuthRequest;
import com.connecter.digitalguiljabiback.security.dto.KakaoAuthResponse;
import com.connecter.digitalguiljabiback.security.dto.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * KakaoAuthClient
 * 카카오 API와의 통신을 담당하는 클라이언트 클래스입니다.
 * 카카오 인증에 관련된 액세스 토큰 요청과 사용자 정보를 가져오는 기능을 제공합니다.
 * 작성자: hyunjin
 * 버전: 1.0.0
 * 작성일: 2023-07-30
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthClient {

    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate;

    /**
     * 카카오 API로 액세스 토큰을 요청합니다.
     * @param request KakaoAuthRequest 객체로부터 인증 정보를 받아옵니다.
     * @return 요청한 액세스 토큰 문자열
     * @throws KakaoClientException 카카오 API 호출 중 예외가 발생했을 경우
     */
    public String requestKakaoAccessToken(KakaoAuthRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = request.makeBody();
        body.add("grant_type", kakaoProperties.getGrantType());
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", kakaoProperties.getRedirectUri());
        body.add("client_secret", kakaoProperties.getClientSecret());

        HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);


        try {
            KakaoAuthResponse response = restTemplate.postForObject(kakaoProperties.getTokenUri(), httpEntity, KakaoAuthResponse.class);
            if (response == null || response.getAccessToken() == null) {
                throw new KakaoClientException("카카오 API에서 액세스 토큰을 가져오지 못했습니다.");
            }
            return response.getAccessToken();
        } catch (RestClientException e) {
            throw new KakaoClientException("카카오 API에서 액세스 토큰 요청 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 카카오 API로부터 사용자 정보를 요청합니다.
     * @param accessToken 요청할 사용자의 액세스 토큰 문자열
     * @return 사용자 정보를 담고 있는 KakaoUserInfoResponse 객체
     * @throws KakaoClientException 카카오 API 호출 중 예외가 발생했을 경우
     */
    public KakaoUserResponse requestKakaoUserInfo(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.profile\"]");

        HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);

        try {
            return restTemplate.postForObject(kakaoProperties.getUserInfoUri(), httpEntity, KakaoUserResponse.class);
        } catch (RestClientException e) {
            throw new KakaoClientException("카카오 API에서 사용자 정보 요청 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
    }
}

