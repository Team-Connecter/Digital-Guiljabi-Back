package com.connecter.digitalguiljabiback.dto.board.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApproveBoardRequest {
  private List<Long> categoryPkList;
  Boolean isCertified;
}
