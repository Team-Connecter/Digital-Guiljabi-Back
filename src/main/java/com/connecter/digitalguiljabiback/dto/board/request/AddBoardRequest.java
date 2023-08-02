package com.connecter.digitalguiljabiback.dto.board.request;

import com.connecter.digitalguiljabiback.dto.board.CardDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddBoardRequest {
  @NotNull(message = "제목은 필수항목입니다.")
  private String title;

  @NotNull(message = "소개말은 필수항목입니다.")
  private String introduction;

  //null가능?
  private String thumbnail;

  @NotNull(message = "내용은 필수항목입니다.")
  private CardDto[] cards;

  @NotNull(message = "태그는 필수항목입니다.")
  private String[] tags;

  @NotNull(message = "출처는 필수항목입니다.")
  private String[] sources;
}
