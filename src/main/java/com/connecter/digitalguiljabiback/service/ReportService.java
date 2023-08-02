package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.Report;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.AdminBriefBoardInfo;
import com.connecter.digitalguiljabiback.dto.board.response.AdminBoardListResponse;
import com.connecter.digitalguiljabiback.dto.report.BriefReportResponse;
import com.connecter.digitalguiljabiback.dto.report.ReportBoardListResponse;
import com.connecter.digitalguiljabiback.dto.report.ReportRequest;
import com.connecter.digitalguiljabiback.exception.ReportDuplicatedException;
import com.connecter.digitalguiljabiback.repository.BoardRepository;
import com.connecter.digitalguiljabiback.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReportService {

  private final ReportRepository reportRepository;
  private final BoardRepository boardRepository;


  public void addReport(Users user, Long boardPk, ReportRequest request) {
    Board board = boardRepository.findById(boardPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 pk의 board가 존재하지 않습니다"));

    //해당하는 user가 같은 글을 여러번 신고한 이력이 있는지 확인 - 이미 신고한 글입니다
    Report report = reportRepository.findByUserAndBoard(user, board)
      .orElseGet(() -> null);

    if (report != null)
      throw new ReportDuplicatedException("이미 신고한 이력이 존재합니다");

    //새로 만들기
    report = Report.makeReport(user, board, request.getType(), request.getContent());
    reportRepository.save(report);

    //board의 reportCnt +1
    board.addReportCnt();
  }

  public ReportBoardListResponse findByBoard(Long boardPk) {
    Board board = boardRepository.findById(boardPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 pk의 Board가 존재하지 않습니다"));

    List<Report> reportList = reportRepository.findByBoard(board);

    List<BriefReportResponse> briefReportResponses = BriefReportResponse.convertList(reportList);

    return new ReportBoardListResponse(reportList.size(), briefReportResponses);
  }

  public AdminBoardListResponse getBoardByReport(Integer pageSize, Integer page, Boolean viewHigh5) {
    Pageable pageable = PageRequest.of(page-1, pageSize);

    List<Board> boardList;
    List<LocalDateTime> recentReportedAtList = new ArrayList<>();

    if (viewHigh5) {
      boardList = boardRepository.findByReportCntGreaterThanEqualOrderByReportCnt(5, pageable); //5회 이상인 것만 조회
    } else {
      boardList = boardRepository.findByReportCntGreaterThanEqualOrderByReportCnt(1, pageable); //1회 이상인 것만 조회
    }

    for (Board b: boardList) {
      LocalDateTime ldt = reportRepository.findByRecentReportByBoard(b.getPk())
        .orElseGet(() -> null);
      recentReportedAtList.add(ldt);
    }

    List<AdminBriefBoardInfo> info = AdminBriefBoardInfo.convertList(boardList, recentReportedAtList);

    return new AdminBoardListResponse(boardList.size(), info);
  }
}
