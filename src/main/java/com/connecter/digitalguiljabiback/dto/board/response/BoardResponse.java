package com.connecter.digitalguiljabiback.dto.board.response;

import com.connecter.digitalguiljabiback.dto.board.CardDto;
import com.connecter.digitalguiljabiback.dto.category.CategoryResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
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
  private String thumbnailUrl;
  private String introduction;
  private Long writerPk;
  private String writerName;
  private String writerProfileUrl;
  private LocalDateTime updateAt;
  private int cardCnt;
  private List<CardDto> cards;
  private List<String> sources;
  private List<String> tags;
  private List<CategoryResponse> categories;
  private Long likeCnt;
  private Long bookmarkCnt;
  @JsonProperty("isMine")
  private boolean isMine;
  @JsonProperty("isBookmarked")
  private boolean isBookmarked;
  @JsonProperty("isLiked")
  private boolean isLiked;
}
