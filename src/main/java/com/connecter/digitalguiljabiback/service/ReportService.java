package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.Report;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.AdminBriefBoardInfo;
import com.connecter.digitalguiljabiback.dto.board.response.AdminBoardListResponse;
import com.connecter.digitalguiljabiback.dto.report.BriefReportResponse;
import com.connecter.digitalguiljabiback.dto.report.ReportBoardListResponse;
import com.connecter.digitalguiljabiback.dto.report.ReportRequest;
import com.connecter.digitalguiljabiback.dto.report.ReportSortType;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
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


  public void addReport(Users user, Long boardPk, ReportRequest request) throws NoSuchElementException, ReportDuplicatedException {
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

  public ReportBoardListResponse findByBoard(Long boardPk) throws NoSuchElementException {
    Board board = boardRepository.findById(boardPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 pk의 Board가 존재하지 않습니다"));

    Users user = board.getUser();

    List<Report> reportList = reportRepository.findByBoard(board);

    List<BriefReportResponse> briefReportResponses = BriefReportResponse.convertList(reportList);

    return ReportBoardListResponse
      .builder()
      .boardPk(boardPk)
      .title(board.getTitle())
      .writerPk(user.getPk())
      .writerName(user.getNickname())
      .cnt(reportList.size())
      .repList(briefReportResponses)
      .build();
  }

  public AdminBoardListResponse getBoardByReport(Integer pageSize, Integer page, Boolean viewHigh5, ReportSortType type) {
    Pageable pageable = PageRequest.of(page-1, pageSize);

    List<Board> boardList;
    List<LocalDateTime> recentReportedAtList = new ArrayList<>();

    int moreThan = viewHigh5? 5 : 1;

    if (type == ReportSortType.RECENT) //최신순 정렬
      boardList = boardRepository.findByReportCntGreaterThanEqualOrderByCreateAtDesc(moreThan, pageable); //5회 이상인 것만 조회
    else //신고많은 순 정렬
      boardList = boardRepository.findByReportCntGreaterThanEqualOrderByReportCnt(moreThan, pageable); //5회 이상인 것만 조회

    for (Board b: boardList) {
      LocalDateTime ldt = reportRepository.findByRecentReportByBoard(b.getPk())
        .orElseGet(() -> null);
      recentReportedAtList.add(ldt);
    }

    List<AdminBriefBoardInfo> info = AdminBriefBoardInfo.convertList(boardList, recentReportedAtList);

    return new AdminBoardListResponse(boardList.size(), info);
  }

  public void deleteReport(Users user, Long reportPk) throws NoSuchElementException, ForbiddenException {
    Report report = reportRepository.findById(reportPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 게시글이 없습니다"));

    Users reportUser = report.getUser();
    if (user != reportUser) {
      throw new ForbiddenException("해당 신고를 취소할 권한이 없습니다");
    }

    reportRepository.delete(report);
  }
}
