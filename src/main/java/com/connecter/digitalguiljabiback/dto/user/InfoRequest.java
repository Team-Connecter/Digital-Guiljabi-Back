package com.connecter.digitalguiljabiback.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InfoRequest {
  private String introduction;
  @NotBlank(message = "nickname은 빈 문자열이면 안됩니다")
  private String nickname;
  @NotBlank(message = "id1365는 빈 문자열이면 안됩니다")
  private String id1365;
  @NotBlank(message = "idVms는 빈 문자열이면 안됩니다")
  private String idVms;
}
