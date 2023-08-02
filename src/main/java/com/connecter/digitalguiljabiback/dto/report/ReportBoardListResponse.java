package com.connecter.digitalguiljabiback.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ReportBoardListResponse {
  private int cnt;

  private List<BriefReportResponse> list;
}
