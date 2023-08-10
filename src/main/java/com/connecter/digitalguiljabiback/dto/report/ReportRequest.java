package com.connecter.digitalguiljabiback.dto.report;

import com.connecter.digitalguiljabiback.domain.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {
  @NotNull
  private ReportType type;
  @NotNull
  private String content;
}
