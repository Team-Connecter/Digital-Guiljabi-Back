package com.connecter.digitalguiljabiback.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
