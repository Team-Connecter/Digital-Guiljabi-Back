package com.connecter.digitalguiljabiback.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AllUserResponse {
  private int cnt;
  private List<UsersResponse> list;
}
