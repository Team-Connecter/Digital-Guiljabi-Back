package com.connecter.digitalguiljabiback.dto.board;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddBoardRequest {
  private String title;
  private String introduction;
  private String thumbnail;
  private CardDto[] cards;
  private String[] tags;
  private String[] sources;
}
