package com.connecter.digitalguiljabiback.dto.login.response;

import com.connecter.digitalguiljabiback.dto.login.UserRequest;

public interface Oauth2UserResponse {

  public Object getUid();

  UserRequest toUserRequest();
}
