package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.report.ReportRequest;
import com.connecter.digitalguiljabiback.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ReportController", description = "신고 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class ReportController {
  private final ReportService reportService;

  // 게시글 신고하기
  @PostMapping("/boards/{boardPk}/reports")
  public ResponseEntity report(@AuthenticationPrincipal Users user, @PathVariable Long boardPk, @RequestBody ReportRequest request) {
    reportService.addReport(user, boardPk, request);

    return ResponseEntity.ok().build();
  }

  
}
