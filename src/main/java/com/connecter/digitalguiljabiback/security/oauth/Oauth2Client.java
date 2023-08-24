package com.connecter.digitalguiljabiback.security.oauth;


import com.connecter.digitalguiljabiback.dto.login.AuthRequest;
import com.connecter.digitalguiljabiback.dto.login.response.Oauth2UserResponse;

public interface Oauth2Client {
  String getAuthUrl();

  Oauth2UserResponse getUserInfo(AuthRequest req, String redirectUrl);

}
