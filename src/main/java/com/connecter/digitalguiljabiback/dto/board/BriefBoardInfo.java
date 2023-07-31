package com.connecter.digitalguiljabiback.dto.board;

import com.connecter.digitalguiljabiback.domain.BoardStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BriefBoardInfo {
  private Long boardPk;
  private String title;
  private String thumbnail;
  private LocalDateTime updateAt;
  private String introduction;
  private String[] tag;
  private Long likeCnt;
  private Long bookmarkCnt;
  private BoardStatus status;
  private String reason;

  public void setStatus(BoardStatus status, String reason) {
    this.status = status;
    this.reason = reason;
  }
}
