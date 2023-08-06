package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.*;
import com.connecter.digitalguiljabiback.dto.board.AdminBriefBoardInfo;
import com.connecter.digitalguiljabiback.dto.board.BriefBoardInfo;
import com.connecter.digitalguiljabiback.dto.board.CardDto;
import com.connecter.digitalguiljabiback.dto.board.request.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.request.BoardListRequest;
import com.connecter.digitalguiljabiback.dto.board.response.AdminBoardListResponse;
import com.connecter.digitalguiljabiback.dto.board.response.BoardListResponse;
import com.connecter.digitalguiljabiback.dto.category.AddCategoryRequest;
import com.connecter.digitalguiljabiback.dto.report.ReportRequest;
import com.connecter.digitalguiljabiback.dto.report.ReportSortType;
import com.connecter.digitalguiljabiback.dto.report.response.BriefReportResponse;
import com.connecter.digitalguiljabiback.dto.report.response.ReportBoardListResponse;
import com.connecter.digitalguiljabiback.exception.ReportDuplicatedException;
import com.connecter.digitalguiljabiback.repository.BoardRepository;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReportServiceTest {

  @Autowired
  private BoardService boardService;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ReportService reportService;
  @Autowired
  private UserService userService;

  private final String title = "title";
  private final String introduction = "introduction";
  private final String thumbnail = "asd";
  private CardDto[] cards;
  private String[] tags;
  private String[] sources;
  private String reason;

  private Users user1;
  private Board newBoard;

  @BeforeAll
  void setUp() {
    cards = new CardDto[3];
    for (int i =0; i<3; i++)
      cards[i] = new CardDto("subtitle"+i, "img"+i, "content"+i);

    tags = new String[3];
    for (int i =0; i<3; i++)
      tags[i] = "tag" + i;

    sources = new String[3];
    for (int i =0; i<3; i++)
      sources[i] = "source" + i;

    reason = "마음에 안듦";
  }

  @BeforeEach
  void setUp2() {
    //유저만들기
    user1 = Users.makeUsers("KAKAO12341", "asdf", OauthType.KAKAO);
    userRepository.save(user1);

    //정보글 만들기
    AddBoardRequest request = new AddBoardRequest(title,introduction,thumbnail,cards,tags, sources);
    newBoard = boardService.makeBoard(user1, request);

    //카테고리 만들기
    Category savedCategory = categoryService.add(new AddCategoryRequest("카테고리1", null));
    //승인하기
    boardService.approve(newBoard.getPk(), List.of(new Long[]{savedCategory.getPk()}));
  }

  //신고등록 테스트 (board에 신고횟수 증가 여부)
  @DisplayName("신고 추가")
  @Transactional
  @Order(1)
  @Test
  void addReport() {
    //신고하기
    ReportRequest reportRequest = ReportRequest.builder().type(ReportType.IRREL).content(reason).build();
    Report report = reportService.addReport(user1, newBoard.getPk(), reportRequest);

    assertEquals(1, newBoard.getReportCnt());
    ReportBoardListResponse response = reportService.findByBoard(newBoard.getPk());
    BriefReportResponse briefReportResponse = response.getRepList().get(0);

    assertEquals(reason, briefReportResponse.getReason());
    assertEquals(ReportType.IRREL, briefReportResponse.getType());
    assertEquals(user1.getNickname(), briefReportResponse.getUsername());
    assertEquals(user1.getPk(), briefReportResponse.getUserPk());
    assertEquals(report.getPk(), briefReportResponse.getReportPk());
    assertEquals(report.getCreateAt(), briefReportResponse.getReportedAt());
  }

  //신고 같은 사람이 같은 board에 2번이상 불가능
  @DisplayName("같은 신고 2번 불가능")
  @Transactional
  @Order(2)
  @Test
  void duplicateReport() {
    //신고하기
    ReportRequest reportRequest = ReportRequest.builder().type(ReportType.IRREL).content(reason).build();
    reportService.addReport(user1, newBoard.getPk(), reportRequest);

    assertThrows(ReportDuplicatedException.class, () -> {
      reportService.addReport(user1, newBoard.getPk(), reportRequest);
    });
  }

  @DisplayName("신고 5회 이상 가려지는지 확인 + 최신순 정렬여부 확인") //Restricted
  @Transactional
  @Order(3)
  @Test
  void hide5More() {

    Users user2 = Users.makeUsers("KAKAO123411", "asdf", OauthType.KAKAO);
    userRepository.save(user2);
    Users user3 = Users.makeUsers("KAKAO12341235", "asdf", OauthType.KAKAO);
    userRepository.save(user3);
    Users user4 = Users.makeUsers("KAKAO123413", "asdf", OauthType.KAKAO);
    userRepository.save(user4);
    Users user5 = Users.makeUsers("KAKAO123414", "asdf", OauthType.KAKAO);
    userRepository.save(user5);

    //5번 신고하기
    ReportRequest reportRequest = ReportRequest.builder().type(ReportType.IRREL).content(reason).build();
    reportService.addReport(user1, newBoard.getPk(), reportRequest);
    reportService.addReport(user2, newBoard.getPk(), reportRequest);
    reportService.addReport(user3, newBoard.getPk(), reportRequest);
    reportService.addReport(user4, newBoard.getPk(), reportRequest);
    reportService.addReport(user5, newBoard.getPk(), reportRequest);

    List<Users> userList = List.of(new Users[]{user1, user2, user3, user4, user5});

    //최신순 정렬 확인
    ReportBoardListResponse byBoard = reportService.findByBoard(newBoard.getPk());
    List<BriefReportResponse> repList = byBoard.getRepList();
    for (int i = 0; i< repList.size(); i++) {
      assertEquals(repList.get(i).getUserPk(), userList.get(i).getPk());
    }

    assertEquals(BoardStatus.RESTRICTED, newBoard.getStatus());
  }

  @DisplayName("회원 탈퇴 시 user가 null이 되는지 테스트")
  @Transactional
  @Order(4)
  @Test
  void reportUserNull() {
    //신고하기
    ReportRequest reportRequest = ReportRequest.builder().type(ReportType.IRREL).content(reason).build();
    reportService.addReport(user1, newBoard.getPk(), reportRequest);

    userService.delete(user1);

    ReportBoardListResponse byBoard = reportService.findByBoard(newBoard.getPk());
    List<BriefReportResponse> repList = byBoard.getRepList();

    for (BriefReportResponse r: repList) {
      assertNull(r.getUserPk());
      assertNull(r.getUsername());
    }
  }

  //신고 5회 이상만 조회되는지 확인
  @DisplayName("신고 5회 이상만 조회되는지 확인 + 삭제되는지 확인")
  @Transactional
  @Order(5)
  @Test
  void get5MoreReoprt() {
    Users user2 = Users.makeUsers("KAKAO123411", "asdf", OauthType.KAKAO);
    userRepository.save(user2);
    Users user3 = Users.makeUsers("KAKAO12341235", "asdf", OauthType.KAKAO);
    userRepository.save(user3);
    Users user4 = Users.makeUsers("KAKAO123413", "asdf", OauthType.KAKAO);
    userRepository.save(user4);
    Users user5 = Users.makeUsers("KAKAO123414", "asdf", OauthType.KAKAO);
    userRepository.save(user5);

    //5번 신고하기
    ReportRequest reportRequest = ReportRequest.builder().type(ReportType.IRREL).content(reason).build();
    Report report = reportService.addReport(user1, newBoard.getPk(), reportRequest);
    reportService.addReport(user2, newBoard.getPk(), reportRequest);
    reportService.addReport(user3, newBoard.getPk(), reportRequest);
    reportService.addReport(user4, newBoard.getPk(), reportRequest);
    reportService.addReport(user5, newBoard.getPk(), reportRequest);

    AdminBoardListResponse boardByReport = reportService.getBoardByReport(10, 1, true, null);
    AdminBriefBoardInfo info = boardByReport.getList().get(0);
    assertEquals(newBoard.getPk(), info.getBoardPk());

    //삭제테스트
    Long pk = report.getPk();
    reportService.deleteReport(user1, report.getPk());

    ReportBoardListResponse byBoard = reportService.findByBoard(newBoard.getPk());
    assertEquals(4, newBoard.getReportCnt());
    boolean hasData = false;
    for (BriefReportResponse r :  byBoard.getRepList()) {
      if (r.getReportPk() == pk) {
        hasData = true;
        break;
      }
    }
    assertFalse(hasData);

    //전체삭제 테스트
    reportService.deleteAllReport(newBoard.getPk());
    byBoard = reportService.findByBoard(newBoard.getPk());
    assertEquals(0, newBoard.getReportCnt());
    assertEquals(0, byBoard.getRepList().size());

  }
}



