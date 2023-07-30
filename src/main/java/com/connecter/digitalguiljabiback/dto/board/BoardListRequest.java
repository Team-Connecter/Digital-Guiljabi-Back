package com.connecter.digitalguiljabiback.dto.board;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardListRequest {
  //전부 nullable
  private Long categoryPk;
  private String search;
  @Min(value = 2, message = "page 크기는 1보다 커야합니다")
  private Integer pageSize;
  @Min(value = 1, message = "page는 0보다 커야합니다")
  private Integer page;
  private SortType sortType;
}
