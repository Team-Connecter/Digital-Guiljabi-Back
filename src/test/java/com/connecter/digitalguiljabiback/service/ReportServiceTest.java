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

    assertEquals(BoardStatus.RESTRICTED, newBoard.getStatus());
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

  @DisplayName("신고 5회 이상 가려지는지 확인") //Restricted
  @Transactional
  @Order(3)
  @Test
  void hide5More() {
    //신고하기
    ReportRequest reportRequest = ReportRequest.builder().type(ReportType.IRREL).content(reason).build();
    reportService.addReport(user1, newBoard.getPk(), reportRequest);

    assertThrows(ReportDuplicatedException.class, () -> {
      reportService.addReport(user1, newBoard.getPk(), reportRequest);
    });
  }

  @DisplayName("회원 탈퇴 시 user가 null이 되는지 테스트") //Restricted
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




}

//신고 삭제

//신고 수정

//신고 모두 삭제(board에 신고횟수~)

//신고 최신순 정렬 조회 확인

//신고 5회 이상만 조회되는지 확인


