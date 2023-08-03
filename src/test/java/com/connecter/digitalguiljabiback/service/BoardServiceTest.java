package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.BoardStatus;
import com.connecter.digitalguiljabiback.domain.OauthType;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.CardDto;
import com.connecter.digitalguiljabiback.dto.board.request.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.response.BoardResponse;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

  @DisplayName("게시글 생성") //게시글 생성 시 엔티티가 잘 만들어지는지, get했을 때도 잘 불러와지는지 확인
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
}