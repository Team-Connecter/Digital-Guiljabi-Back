package com.connecter.digitalguiljabiback.dto.board;

import com.connecter.digitalguiljabiback.domain.Board;
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
public class AdminBriefBoardInfo {
  private Long boardPk;
  private String title;
  private Long writerPk;
  private String writerName;
  private LocalDateTime recentReportAt;
  private int reportCnt;

  public static List<AdminBriefBoardInfo> convertList(List<Board> list, List<LocalDateTime> recentReportedAtList) {
    List<AdminBriefBoardInfo> breifList = new ArrayList<>();

    for (int i =0; i<list.size(); i++) {
      Board b = list.get(i);

      AdminBriefBoardInfo brbi;
      Users user = b.getUser();

      //내 데이터면 -> 태그 필요 x
      brbi= AdminBriefBoardInfo.builder()
        .boardPk(b.getPk())
        .title(b.getTitle())
        .writerPk(user.getPk())
        .writerName(user.getNickname())
        .recentReportAt(recentReportedAtList.get(i))
        .reportCnt(b.getReportCnt())
        .build();

      breifList.add(brbi);
    }

    return breifList;
  }


}
