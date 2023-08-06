package com.connecter.digitalguiljabiback.dto.user.response;

import com.connecter.digitalguiljabiback.domain.UserRole;
import com.connecter.digitalguiljabiback.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UsersResponse {
  private Long pk;
  private String nickname;
  private LocalDateTime joinAt;
  private String profileImgUrl;
  private String introduction;
  private String id1365;
  private String idVms;
  private UserRole role;

  public static List<UsersResponse> convertList(List<Users> userList) {
    List<UsersResponse> responses = new ArrayList<>();
    for (Users u: userList) {
      UsersResponse ur = UsersResponse.builder()
        .pk(u.getPk())
        .nickname(u.getNickname())
        .joinAt(u.getCreateAt())
        .profileImgUrl(u.getProfileUrl())
        .introduction(u.getIntroduction())
        .id1365(u.getId1365())
        .idVms(u.getIdvms())
        .role(u.getRole())
        .build();

      responses.add(ur);
    }

    return responses;
  }
}
