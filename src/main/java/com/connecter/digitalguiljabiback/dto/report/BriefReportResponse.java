package com.connecter.digitalguiljabiback.dto.report;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.Report;
import com.connecter.digitalguiljabiback.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BriefReportResponse {
  private Long reportPk;
  private LocalDateTime reportedAt;
  private Long userPk;
  private String username;
  private String reason;

  public static List<BriefReportResponse> convertList(List<Report> boardList) {
    List<BriefReportResponse> list = new ArrayList<>();
    for (Report r: boardList) {
      Users user = r.getUser();
      BriefReportResponse br = new BriefReportResponse(r.getPk(), r.getCreateAt(), user.getPk(), user.getNickname(), r.getContent());
      list.add(br);
    }

    return list;
  }
}
