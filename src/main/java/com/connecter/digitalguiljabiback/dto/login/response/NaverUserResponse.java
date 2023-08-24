package com.connecter.digitalguiljabiback.dto.login.response;

import com.connecter.digitalguiljabiback.dto.login.UserRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 네이버API 서버로부터 받은 응답객체
 * 사용자의 닉네임 정보를 제공
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserResponse implements Oauth2UserResponse {
    @JsonProperty("response")
    private ResponseData response;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ResponseData {
        private String id;
    }

    public String getUid() {
        return response.getId();
    }


    public UserRequest toUserRequest() {
        return new UserRequest(this.response.id);
    }

}
