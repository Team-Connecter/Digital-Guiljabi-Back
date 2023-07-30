package com.connecter.digitalguiljabiback.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApproveBoardRequest {
  private Long boardPk;

  private List<Long> categoryPkList;
}
