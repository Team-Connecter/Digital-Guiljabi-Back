package com.connecter.digitalguiljabiback.dto.board.request;

import com.connecter.digitalguiljabiback.dto.board.SortType;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardListRequest {
  //전부 nullable
  private Long categoryPk;
  private String q;
  @Min(value = 2, message = "page 크기는 1보다 커야합니다")
  private Integer pageSize;
  @Min(value = 1, message = "page는 0보다 커야합니다")
  private Integer page;
  private SortType sort;
}
