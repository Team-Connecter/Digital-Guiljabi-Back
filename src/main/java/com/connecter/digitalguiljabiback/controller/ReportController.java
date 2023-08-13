package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.response.AdminBoardListResponse;
import com.connecter.digitalguiljabiback.dto.report.response.MyReportListResponse;
import com.connecter.digitalguiljabiback.dto.report.response.ReportBoardListResponse;
import com.connecter.digitalguiljabiback.dto.report.ReportRequest;
import com.connecter.digitalguiljabiback.dto.report.ReportSortType;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.exception.ReportDuplicatedException;
import com.connecter.digitalguiljabiback.service.BoardService;
import com.connecter.digitalguiljabiback.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@Tag(name = "신고", description = "신고 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class ReportController {
  private final ReportService reportService;
  private final BoardService boardService;

  @Operation(summary = "정보글 신고하기", description = """
    [로그인 필요] ① IRREL: 내용과 크게다름 ② COPYR: 저작권 침해 ③ NOXI: 유해,광고성 ④ ETC: 기타<br>
    200: 성공<br>
    400: 잘못된 요청 - type과 content는 필수<br>
    404: 해당 pk의 정보글이 존재하지 않음<br>
    409: 이미 신고한 글임
    """)
  @PostMapping("/boards/{boardPk}/reports")
  public ResponseEntity report(
    @AuthenticationPrincipal Users user,
    @PathVariable Long boardPk,
    @RequestBody @Valid ReportRequest request
  ) throws NoSuchElementException, ReportDuplicatedException {

    reportService.addReport(user, boardPk, request);

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "신고 취소하기", description = """
    [로그인 필요]<br>
    200: 성공<br>
    400: 잘못된 요청<br>
    403: 해당 신고를 취소할 권한이 없다<br>
    404: 해당 pk의 신고가 존재하지 않음
    """)
  @DeleteMapping("/reports/{reportPk}")
  public ResponseEntity cancelReport(@AuthenticationPrincipal Users user, @PathVariable Long reportPk) throws NoSuchElementException, ForbiddenException {
    reportService.deleteReport(user, reportPk);

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "내가 신고한 글 목록 조회", description = """
    [로그인 필요]<br>200: 성공
    """)
  @GetMapping("/reports/my")
  public ResponseEntity getMyReport(@AuthenticationPrincipal Users user) {
    MyReportListResponse myReport = reportService.getMyReport(user);

    return ResponseEntity.ok(myReport);
  }
  
  //ADMIN ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
  @Operation(summary = "신고 횟수가 많은 순으로 정보글 조회", description = """
    [관리자] 신고 횟수가 많은 순으로 정보글을 조회합니다. 신고횟수 5회 이상만 조회도 가능<br>
    200: 성공
    """)
  @Parameter(name = "viewHigh5", description = "신고횟수 5회 이상만 조회할 지 결정")
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

  @Operation(summary = "정보글에 대한 신고 목록 조회", description = """
    [관리자]<br>
    200: 성공
    404: 해당 pk의 정보글이 존재하지 않음
    """)
  @GetMapping("/admin/boards/{boardPk}/reports")
  public ResponseEntity<ReportBoardListResponse> getReportByBoard(@PathVariable Long boardPk) {
    ReportBoardListResponse byBoard = reportService.findByBoard(boardPk);

    return ResponseEntity.ok(byBoard);
  }

  @Operation(summary = "신고내역 초기화(전부 없애기)", description = """
    [관리자] 정보글의 신고내역을 초기화하여 전부 없앱니다.<br>
    200: 성공
    404: 해당 pk의 정보글이 존재하지 않음
    """)
  @DeleteMapping("/admin/boards/{boardPk}/reports")
  public ResponseEntity deleteAllReport(@PathVariable Long boardPk) {
    reportService.deleteAllReport(boardPk);

    return ResponseEntity.ok().build();
  }
}
