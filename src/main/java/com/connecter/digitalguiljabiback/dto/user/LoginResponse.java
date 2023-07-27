package com.connecter.digitalguiljabiback.dto.user;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponse {
  private String token;
  private LoginResponse.UserInfo user;

  public LoginResponse(String token, String id, String username) {
    this.token = token;
    this.user = new LoginResponse.UserInfo(id, username);
  }

  @Data
  @AllArgsConstructor
  private class UserInfo {
    private String id;
    private String username;
  }
}