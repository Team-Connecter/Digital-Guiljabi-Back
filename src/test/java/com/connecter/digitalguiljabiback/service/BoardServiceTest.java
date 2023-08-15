package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.*;
import com.connecter.digitalguiljabiback.domain.board.Board;
import com.connecter.digitalguiljabiback.domain.board.BoardStatus;
import com.connecter.digitalguiljabiback.dto.board.BriefBoardInfo;
import com.connecter.digitalguiljabiback.dto.board.CardDto;
import com.connecter.digitalguiljabiback.dto.board.request.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.request.BoardListRequest;
import com.connecter.digitalguiljabiback.dto.board.response.BoardListResponse;
import com.connecter.digitalguiljabiback.dto.board.response.BoardResponse;
import com.connecter.digitalguiljabiback.dto.category.AddCategoryRequest;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.repository.BoardRepository;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BoardServiceTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private BoardService boardService;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BoardRepository boardRepository;

  private final String title = "title";
  private final String introduction = "introduction";
  private final String thumbnail = "asd";
  private CardDto[] cards;
  private List<String> tags;
  private List<String> sources;
  private String reason;


  @BeforeAll
  void setUp() {
    cards = new CardDto[3];
    for (int i =0; i<3; i++)
      cards[i] = new CardDto("subtitle"+i, "img"+i, "content"+i);

    tags = new ArrayList<>();
    for (int i =0; i<3; i++)
      tags.add("tag" + i);

    sources = new ArrayList<>();
    for (int i =0; i<3; i++)
      sources.add("source" + i);

    reason = "마음에 안듦";
  }

  @DisplayName("정보글 생성") //정보글 생성 시 엔티티가 잘 만들어지는지, get했을 때도 잘 불러와지는지 확인
  @Transactional
  @Order(1)
  @Test
  void makeBoard() {
    Users user = Users.makeUsers("KAKAO1234", "asdf", OauthType.KAKAO);
    user = userRepository.save(user);
    AddBoardRequest request = new AddBoardRequest(title,introduction,thumbnail,cards,tags, sources);
    Board newBoard = boardService.makeBoard(user, request);

    assertEquals(title, newBoard.getTitle());
    assertEquals(introduction, newBoard.getIntroduction());
    assertEquals(thumbnail, newBoard.getThumbnailUrl());
    assertEquals(user, newBoard.getUser());
    assertEquals(BoardStatus.WAITING, newBoard.getStatus());
    assertEquals(newBoard.getUpdateAt(), newBoard.getCreateAt());
    assertEquals(0L, newBoard.getBookmarkCnt());
    assertEquals(0L, newBoard.getLikeCnt());
    assertEquals(0, newBoard.getReportCnt());
    assertNull(newBoard.getReason());

    BoardResponse boardInfo = boardService.getBoardInfo(newBoard.getPk(), user);

    assertEquals(title, boardInfo.getTitle());
    assertEquals(user.getPk(), boardInfo.getWriterPk());
    assertEquals(user.getNickname(), boardInfo.getWriterName());
    assertEquals(3, boardInfo.getCardCnt());
    for (int i =0; i< sources.size(); i++)
      assertEquals(sources.get(i), boardInfo.getSources().get(i));

    assertEquals(0L, boardInfo.getLikeCnt());
    assertEquals(0L, boardInfo.getBookmarkCnt());
    for (int i =0; i< tags.size(); i++)
      assertEquals(tags.get(i), boardInfo.getTags().get(i));

    for (int i =0; i<cards.length; i++) {
      List<CardDto> getCard = boardInfo.getCards();
      assertEquals(cards[i].getContent(), getCard.get(i).getContent());
      assertEquals(cards[i].getImgUrl(), getCard.get(i).getImgUrl());
      assertEquals(cards[i].getSubTitle(), getCard.get(i).getSubTitle());
    }
  }

  //정보글 삭제: 자기 자신만 가능하게
  @DisplayName("정보글 삭제")
  @Transactional
  @Order(2)
  @Test
  void deleteBoard() {
    Users user1 = Users.makeUsers("KAKAO12341", "asdf", OauthType.KAKAO);
    user1 = userRepository.save(user1);
    Users user2 = Users.makeUsers("KAKAO12342", "asdf", OauthType.KAKAO);
    user2 = userRepository.save(user2);

    AddBoardRequest request = new AddBoardRequest(title,introduction,thumbnail,cards,tags, sources);

    Board newBoard = boardService.makeBoard(user1, request);

    Users finalUser = user2;
    assertThrows(ForbiddenException.class, () -> {
      boardService.deleteBoard(finalUser, newBoard.getPk());
    });

    boardService.deleteBoard(user1, newBoard.getPk());

    Users finalUser1 = user1;
    assertThrows(NoSuchElementException.class, () -> {
      boardService.getBoardInfo(newBoard.getPk(), finalUser1);
    });
  }

  //제목으로 검색 잘 되는지 확인
  @DisplayName("정보글 제목으로 검색")
  @Transactional
  @Order(3)
  @Test
  void searchByTitle() {
    Users user = Users.makeUsers("KAKAO1234", "asdf", OauthType.KAKAO);
    user = userRepository.save(user);
    AddBoardRequest request = new AddBoardRequest(title,introduction,thumbnail,cards,tags, sources);
    boardService.makeBoard(user, request);

    BoardListRequest boardListRequest = BoardListRequest.makeRequest(null, title, 10, 1, null);
    BoardListResponse boardList = boardService.getBoardList(boardListRequest, BoardStatus.WAITING);

    boolean hasData = false;
    for (BriefBoardInfo info: boardList.getList()) {
      if (info.getTitle().equals(title)) {
        hasData = true;
        break;
      }
    }

    assertTrue(hasData);
  }

  //정보글 승인 + 카테고리 등록 + 조회 확인
  @DisplayName("정보글 승인, 카테고리 등록")
  @Transactional
  @Order(4)
  @Test
  void approveBoard() {
    Users user1 = Users.makeUsers("KAKAO12341", "asdf", OauthType.KAKAO);
    userRepository.save(user1);

    AddBoardRequest request = new AddBoardRequest(title,introduction,thumbnail,cards,tags, sources);

    Board newBoard = boardService.makeBoard(user1, request);

    //카테고리 만들기
    Category savedCategory = categoryService.add(new AddCategoryRequest("카테고리1", null));

    //board 승인목록 조회 - 데이터가 없어야함
    BoardListRequest request2 = BoardListRequest.makeRequest(null, null, 10, 1, null);
    BoardListResponse approvedBoardList = boardService.getApprovedBoardList(request2);

    boolean hasData = false;
    for (BriefBoardInfo info: approvedBoardList.getList()) {
      if (info.getTitle().equals(title)) {
        hasData = true;
        break;
      }
    }

    assertFalse(hasData);

    //승인하기
    boardService.approve(newBoard.getPk(), List.of(new Long[]{savedCategory.getPk()}));

    //board 승인목록 조회 - 데이터가 있어야함
    BoardListRequest request3 = BoardListRequest.makeRequest(null, title, 10, 1, null);
    approvedBoardList = boardService.getApprovedBoardList(request3);

    hasData = false;
    Long findBoardPk = 0L;
    for (BriefBoardInfo info: approvedBoardList.getList()) {
      if (info.getTitle().equals(title)) {
        hasData = true;
        findBoardPk = info.getBoardPk();
        break;
      }
    }

    assertTrue(hasData);

    BoardResponse boardInfo = boardService.getBoardInfo(findBoardPk, null);
    assertEquals("카테고리1", boardInfo.getCategories().get(0).getName());

  }

  //정보글 승인거부
  @DisplayName("정보글 승인거부")
  @Transactional
  @Order(5)
  @Test
  void rejectBoard() {
    Users user1 = Users.makeUsers("KAKAO12341", "asdf", OauthType.KAKAO);
    userRepository.save(user1);

    AddBoardRequest request = new AddBoardRequest(title,introduction,thumbnail,cards,tags, sources);

    Board newBoard = boardService.makeBoard(user1, request);

    //카테고리 만들기
    Category savedCategory = categoryService.add(new AddCategoryRequest("카테고리1", null));

    //승인거부하기
    boardService.reject(newBoard.getPk(), reason);

    //board 승인목록 조회 - 데이터가 없어야함
    BoardListRequest request3 = BoardListRequest.makeRequest(null, null, 10, 1, null);
    BoardListResponse approvedBoardList = boardService.getApprovedBoardList(request3);

    boolean hasData = false;
    for (BriefBoardInfo info: approvedBoardList.getList()) {
      if (info.getTitle().equals(title)) {
        hasData = true;
        break;
      }
    }

    assertFalse(hasData);

    //거부된 애들 조회
    BoardListRequest listRequest = BoardListRequest.makeRequest(null, null, null, null, null);
    BoardListResponse boardList = boardService.getBoardList(listRequest, BoardStatus.REFUSAL);

    hasData = false;
    for (BriefBoardInfo info: boardList.getList()) {
      if (info.getTitle().equals(title)) {
        hasData = true;
        break;
      }
    }
    assertTrue(hasData);

    //내 꺼 조회
    BoardListResponse myList = boardService.getMyList(user1);
    hasData = false;
    for (BriefBoardInfo info: myList.getList()) {
      if (info.getTitle().equals(title)) {
        hasData = true;
        assertEquals(info.getStatus(), BoardStatus.REFUSAL);
        break;
      }
    }

    assertTrue(hasData);
  }

  //태그로 검색 잘되는지 확인

  //정보글 수정

  //좋아요순 정렬






}