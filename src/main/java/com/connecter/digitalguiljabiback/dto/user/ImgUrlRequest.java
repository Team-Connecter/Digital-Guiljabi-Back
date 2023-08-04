package com.connecter.digitalguiljabiback.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ImgUrlRequest {
  @NotNull
  private String imgUrl;
}
