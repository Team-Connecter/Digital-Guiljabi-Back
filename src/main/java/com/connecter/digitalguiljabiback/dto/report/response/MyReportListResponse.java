package com.connecter.digitalguiljabiback.dto.report.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class MyReportListResponse {
  private int cnt;
  private List<MyReportResponse> list;
}
