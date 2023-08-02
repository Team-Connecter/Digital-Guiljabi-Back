package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.SortType;
import com.connecter.digitalguiljabiback.dto.board.response.AdminBoardListResponse;
import com.connecter.digitalguiljabiback.dto.board.response.BoardListResponse;
import com.connecter.digitalguiljabiback.dto.report.ReportBoardListResponse;
import com.connecter.digitalguiljabiback.dto.report.ReportRequest;
import com.connecter.digitalguiljabiback.dto.report.ReportSortType;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.exception.ReportDuplicatedException;
import com.connecter.digitalguiljabiback.service.BoardService;
import com.connecter.digitalguiljabiback.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "ReportController", description = "신고 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class ReportController {
  private final ReportService reportService;
  private final BoardService boardService;

  //게시글 신고하기
  @PostMapping("/boards/{boardPk}/reports")
  public ResponseEntity report(
    @AuthenticationPrincipal Users user,
    @PathVariable Long boardPk,
    @RequestBody ReportRequest request
  ) throws NoSuchElementException, ReportDuplicatedException {

    reportService.addReport(user, boardPk, request);

    return ResponseEntity.ok().build();
  }

  //게시글 신고 취소하기
  @DeleteMapping("/reports/{reportPk}")
  public ResponseEntity cancelReport(@AuthenticationPrincipal Users user, @PathVariable Long reportPk) throws NoSuchElementException, ForbiddenException {
    reportService.deleteReport(user, reportPk);

    return ResponseEntity.ok().build();
  }
  
  //ADMIN ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
  //신고 많은 순으로 board 조회(5회 이상만 보기 추가)
  @GetMapping("/admin/boards/top-reported")
  public ResponseEntity getBoardByReport(
    @RequestParam(required = false, defaultValue = "10") Integer pageSize ,
    @RequestParam(required = false, defaultValue = "1") Integer page,
    @RequestParam(required = false, defaultValue = "false") Boolean viewHigh5,
    @RequestParam(required = false, defaultValue = "RECENT") ReportSortType sort
  ) {
    AdminBoardListResponse response = reportService.getBoardByReport(pageSize, page, viewHigh5, sort);

    return ResponseEntity.ok(response);
  }

  //정보글에 대한 신고 목록 조회
  @GetMapping("/admin/boards/{boardPk}/reports")
  public ResponseEntity<ReportBoardListResponse> getReportByBoard(@PathVariable Long boardPk) {
    ReportBoardListResponse byBoard = reportService.findByBoard(boardPk);

    return ResponseEntity.ok(byBoard);
  }

}
