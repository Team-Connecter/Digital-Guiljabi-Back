package com.connecter.digitalguiljabiback.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UsersInfoResponse {
  private String introduction;
  private String nickname;
  private LocalDateTime joinAt;
  private String id21365;
  private String idVms;
  private String imgUrl;
}
