package com.connecter.digitalguiljabiback.dto.board;

import com.connecter.digitalguiljabiback.domain.board.Board;
import com.connecter.digitalguiljabiback.domain.board.BoardStatus;
import com.connecter.digitalguiljabiback.domain.Tag;
import com.connecter.digitalguiljabiback.domain.Users;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BriefBoardInfo {
  private Long boardPk;
  private String title;
  private String username;
  private String thumbnail;
  private LocalDateTime updateAt;
  private String introduction;
  private String[] tag;
  private Long likeCnt;
  private Long bookmarkCnt;
  private BoardStatus status;
  private String reason;
  private Boolean isCertified;

  public void setStatus(BoardStatus status, String reason) {
    this.status = status;
    this.reason = reason;
  }

  //내 데이터 조회
  public static List<BriefBoardInfo> convertList(List<Board> list) {
    List<BriefBoardInfo> breifList = new ArrayList<>();

    for (Board b: list) {
      BriefBoardInfo brbi;

      //내 데이터면 -> 태그 필요 x
      brbi= BriefBoardInfo.builder()
        .boardPk(b.getPk())
        .title(b.getTitle())
        .thumbnail(b.getThumbnailUrl())
        .updateAt(b.getUpdateAt())
        .likeCnt(b.getLikeCnt())
        .isCertified(b.isCertified())
        .bookmarkCnt(b.getBookmarkCnt())
        .status(b.getStatus())
        .reason(b.getReason())
        .build();

      breifList.add(brbi);
    }

    return breifList;
  }

  //전체 조회
  public static List<BriefBoardInfo> convertList(List<Board> list, List<List<Tag>> tagList, List<Users> userList) {
    List<BriefBoardInfo> breifList = new ArrayList<>();

    for (int i =0; i<list.size(); i++) {
      Board b = list.get(i);

      BriefBoardInfo brbi;

      //전체 조회면 -> 태그 필요
      brbi = BriefBoardInfo.builder()
        .boardPk(b.getPk())
        .title(b.getTitle())
        .username(userList.get(i).getNickname())
        .thumbnail(b.getThumbnailUrl())
        .updateAt(b.getUpdateAt())
        .introduction(b.getIntroduction())
        .tag(tagList.get(i).stream()
          .map(Tag::getName)
          .toArray(String[]::new)
        )
        .likeCnt(b.getLikeCnt())
        .bookmarkCnt(b.getBookmarkCnt())
        .isCertified(b.isCertified())
        .build();

      breifList.add(brbi);
    }

    return breifList;
  }



}
