package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.*;
import com.connecter.digitalguiljabiback.dto.board.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.CardDto;
import com.connecter.digitalguiljabiback.repository.BoardRepository;
import com.connecter.digitalguiljabiback.repository.TagRepository;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Transactional
@Service
public class BoardService {

  private final BoardRepository boardRepository;
  private final TagRepository tagRepository;
  private final UserRepository userRepository;

  public void makeBoard(Users user, AddBoardRequest addBoardRequest) {
    //굳이 안넣어도 될듯
    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new NoSuchElementException("해당하는 사용자가 없습니다"));

    List<BoardContent> boardContents = new ArrayList<>();

    String[] source = addBoardRequest.getSources();
    String sourceText = String.join("\tl\tL\t@ls", source);

    Board board = Board.builder()
      .user(findUser)
      .title(addBoardRequest.getTitle())
      .thumbnailUrl(addBoardRequest.getThumbnail())
      .introduction(addBoardRequest.getIntroduction())
      .sources(sourceText)
      .build();

    List<BoardTag> boardTags = new ArrayList<>();

    for (String tag: addBoardRequest.getTags()) {
      Tag findTag = getTag(tag);
      BoardTag boardTag = BoardTag.makeBoardTag(board, findTag);
      boardTags.add(boardTag);
    }

    board.setBoardTags(boardTags);

    for (CardDto card : addBoardRequest.getCards()) {
      boardContents.add(
        BoardContent.makeBoardContent(board, card.getSubTitle(), card.getImgUrl(), card.getContent())
      );
    }

    board.setContents(boardContents);

    boardRepository.save(board);
  }

  private Tag getTag(String tagName) {
    Tag findTag = tagRepository.findByName(tagName)
      .orElseGet(() -> null);

    if (findTag == null) {
      findTag = Tag.makeTag(tagName);
      findTag = tagRepository.save(findTag);
    }

    return findTag;
  }
}
