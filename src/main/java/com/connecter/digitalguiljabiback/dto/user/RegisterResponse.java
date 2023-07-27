package com.connecter.digitalguiljabiback.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class RegisterResponse {
  private String id;
  private String username;
}