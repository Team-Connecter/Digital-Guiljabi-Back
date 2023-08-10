package com.connecter.digitalguiljabiback.dto.boardVersion;

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
public class VersionInfo {
  private String title;
  private String introduction;
  private LocalDateTime createAt;
  private int cardCnt;
  private List<CardDto> cards;
  private List<String> sources;
  private List<String> tags;
}
