package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.board.Board;
import com.connecter.digitalguiljabiback.domain.board.BoardVersion;
import com.connecter.digitalguiljabiback.domain.board.BoardVersionContent;
import com.connecter.digitalguiljabiback.domain.board.VersionDiff;
import com.connecter.digitalguiljabiback.dto.board.CardDto;
import com.connecter.digitalguiljabiback.dto.boardVersion.VersionDiffDTO;
import com.connecter.digitalguiljabiback.dto.boardVersion.VersionInfo;
import com.connecter.digitalguiljabiback.dto.boardVersion.VersionListResponse;
import com.connecter.digitalguiljabiback.repository.BoardRepository;
import com.connecter.digitalguiljabiback.repository.board.BoardVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BoardVersionService {

  private final BoardVersionRepository boardVersionRepository;
  private final BoardRepository boardRepository;

  private final String sourceDelim = "\tl\tL\t@ls";

  //board에 대한 버전들 리스트를 출력
  public VersionListResponse getVersionList(Long boardPk) {

    Board board = boardRepository.findById(boardPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 pk의 board가 없습니다"));

    List<BoardVersion> boardVersionList = boardVersionRepository.findByBoardOrderByCreateAtAsc(board);

    return VersionListResponse.convert(boardVersionList);
  }

  //해당 버전의 내용을 출력
  public VersionInfo getVersion(Long boardVersionPk) {
    BoardVersion boardVersion = findBoardVersion(boardVersionPk);

    List<BoardVersionContent> contentList = boardVersion.getBoardVersionContents();

    return VersionInfo.builder()
      .title(boardVersion.getTitle())
      .introduction(boardVersion.getIntroduction())
      .createAt(boardVersion.getCreateAt())
      .cardCnt(contentList.size())
      .cards(CardDto.versionConvert(contentList))
      .sources(List.of(boardVersion.getSources().split(sourceDelim)))
      .tags(List.of(boardVersion.getTags().split("\n")))
      .build();
  }

  //해당 버전의 변경내용 조회
  public VersionDiffDTO getVersionDiff(Long boardVersionPk) {
    BoardVersion boardVersion = findBoardVersion(boardVersionPk);

    return VersionDiffDTO.convert(boardVersion.getVersionDiff());
  }

  private BoardVersion findBoardVersion(Long boardVersionPk) {
    BoardVersion boardVersion = boardVersionRepository.findById(boardVersionPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 pk의 boardVersion이 없습니다"));

    return boardVersion;
  }
}
