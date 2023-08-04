package com.connecter.digitalguiljabiback.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NicknameRequest {
  @NotNull
  private String nickname;
}
