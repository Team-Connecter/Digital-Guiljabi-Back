package com.connecter.digitalguiljabiback.dto.report.response;

import com.connecter.digitalguiljabiback.domain.board.Board;
import com.connecter.digitalguiljabiback.domain.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyReportResponse {
  private Long reportPk;
  private Long boardPk;
  private String title;
  private String introduction;
  private LocalDateTime reportedAt;
  private String reason;

  public static List<MyReportResponse> convertList(List<Report> reportList) {
    List<MyReportResponse> list = new ArrayList<>();
    for (Report r: reportList) {
      Board board = r.getBoard();
      MyReportResponse br = new MyReportResponse(r.getPk(), board.getPk(), board.getTitle(), board.getIntroduction(), r.getCreateAt(), r.getContent());
      list.add(br);
    }

    return list;
  }
}
