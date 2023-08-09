package com.connecter.digitalguiljabiback.dto.board;


import com.connecter.digitalguiljabiback.domain.board.BoardContent;
import com.connecter.digitalguiljabiback.domain.board.BoardVersionContent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CardDto {
  private String subTitle;
  private String imgUrl;
  private String content;

  public static List<CardDto> convert(List<BoardContent> boardContentList) {
    List<CardDto> cardDtoList = new ArrayList<>();

    for (BoardContent boardContent : boardContentList) {
      CardDto cardDto = new CardDto(boardContent.getTitle(), boardContent.getImgUrl(), boardContent.getContent());
      cardDtoList.add(cardDto);
    }

    return cardDtoList;
  }

  public static List<CardDto> versionConvert(List<BoardVersionContent> versionList) {
    List<CardDto> cardDtoList = new ArrayList<>();

    for (BoardVersionContent versionContent : versionList) {
      CardDto cardDto = new CardDto(versionContent.getTitle(), versionContent.getImgUrl(), versionContent.getContent());
      cardDtoList.add(cardDto);
    }

    return cardDtoList;
  }



}
