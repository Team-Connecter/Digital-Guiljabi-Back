package com.connecter.digitalguiljabiback.dto.report;

import com.connecter.digitalguiljabiback.domain.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {
  private ReportType type;
  private String content;
}
