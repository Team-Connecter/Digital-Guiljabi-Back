package com.connecter.digitalguiljabiback.dto.board.response;

import com.connecter.digitalguiljabiback.dto.board.CardDto;
import com.connecter.digitalguiljabiback.dto.category.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponse {
  private String title;
  private String introduction;
  private Long writerPk;
  private String writerName;
  private LocalDateTime updateAt;
  private int cardCnt;
  private List<CardDto> cards;
  private List<String> sources;
  private List<String> tags;
  private List<CategoryResponse> categories;
  private Long likeCnt;
  private Long bookmarkCnt;
}
