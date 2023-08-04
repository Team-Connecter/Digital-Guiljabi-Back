package com.connecter.digitalguiljabiback.dto.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 카카오API 서버로부터 받은 응답객체
 * 사용자의 닉네임 정보를 제공
 */
@Getter
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserResponse {
    @JsonProperty("id")
    private Long uid;

    public Long getUid() {
        return uid;
    }

    public UserRequest toUserRequest() {
        return new UserRequest(uid.toString());
    }
}
