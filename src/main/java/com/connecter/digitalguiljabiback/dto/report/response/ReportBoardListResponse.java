package com.connecter.digitalguiljabiback.dto.report.response;

import com.connecter.digitalguiljabiback.domain.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class ReportBoardListResponse {
  private Long boardPk;
  private String title;
  private Long writerPk;
  private String writerName;
  private int cnt;


  private List<BriefReportResponse> repList;
}
