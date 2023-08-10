package com.connecter.digitalguiljabiback.dto.board.request;

import com.connecter.digitalguiljabiback.dto.board.SortType;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardListRequest {
  //전부 nullable
  private Long categoryPk;
  private String q;
  @Builder.Default
  @Min(value = 2, message = "page 크기는 1보다 커야합니다")
  private Integer pageSize = 10;
  @Builder.Default
  @Min(value = 1, message = "page는 0보다 커야합니다")
  private Integer page = 1;
  private SortType sort;

  public static BoardListRequest makeRequest(Long categoryPk, String q, Integer pageSize, Integer page, SortType sort) {
    return new BoardListRequest(categoryPk, q, pageSize, page, sort);
  }
}
