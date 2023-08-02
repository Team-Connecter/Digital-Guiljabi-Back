package com.connecter.digitalguiljabiback.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserRequest{
  private String uid;
}

