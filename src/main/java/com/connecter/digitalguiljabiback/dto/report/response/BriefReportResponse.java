package com.connecter.digitalguiljabiback.dto.report.response;

import com.connecter.digitalguiljabiback.domain.Report;
import com.connecter.digitalguiljabiback.domain.ReportType;
import com.connecter.digitalguiljabiback.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
  private ReportType type;

  public static List<BriefReportResponse> convertList(List<Report> boardList) {
    List<BriefReportResponse> list = new ArrayList<>();
    for (Report r: boardList) {
      Users user = r.getUser();
      BriefReportResponse br;
      if (user != null)
        br = new BriefReportResponse(r.getPk(), r.getCreateAt(), user.getPk(), user.getNickname(), r.getContent(), r.getReportType());
      else
        br = new BriefReportResponse(r.getPk(), r.getCreateAt(), null, null, r.getContent(), r.getReportType());
      list.add(br);
    }

    return list;
  }
}
