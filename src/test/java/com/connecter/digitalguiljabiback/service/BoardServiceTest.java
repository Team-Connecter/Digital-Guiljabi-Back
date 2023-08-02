package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.BoardStatus;
import com.connecter.digitalguiljabiback.domain.OauthType;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.CardDto;
import com.connecter.digitalguiljabiback.dto.board.request.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.response.BoardResponse;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
  private UserRepository userRepository;

  private final String title = "title";
  private final String introduction = "introduction";
  private final String thumbnail = "asd";
  private CardDto[] cards;
  private String[] tags;
  private String[] sources;


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
    for (int i =0; i< sources.length; i++)
      assertEquals(sources[i], boardInfo.getSources().get(i));

    assertEquals(0L, boardInfo.getLikeCnt());
    assertEquals(0L, boardInfo.getBookmarkCnt());
    for (int i =0; i< tags.length; i++)
      assertEquals(tags[i], boardInfo.getTags().get(i));

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

  //정보글 승인 + 카테고리 등록 + 조회 확인

  //정보글 승인거부

  //정보글 검색 잘되는지 확인

  //정보글 수정








}