package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.*;
import com.connecter.digitalguiljabiback.dto.board.*;
import com.connecter.digitalguiljabiback.dto.category.CategoryResponse;
import com.connecter.digitalguiljabiback.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Transactional
@Service
public class BoardService {

  private final BoardRepository boardRepository;
  private final TagRepository tagRepository;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;
  private final BoardTagRepository boardTagRepository;

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

  public BoardResponse getBoardInfo(Long boardPk) {
    Board board = boardRepository.findById(boardPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 정보글을 찾을 수 없습니다"));

    List<String> tagList = new ArrayList<>();
    for (BoardTag boardTag: board.getBoardTags()) {
      Tag tag = boardTag.getTag();
      tagList.add(tag.getName());
    }

    List<String> sourceList = new ArrayList<>();
    StringTokenizer st = new StringTokenizer(board.getSources(), "\tl\tL\t@ls");
    while (st.hasMoreTokens()) {
      sourceList.add(st.nextToken());
    }

    List<CardDto> cardDtoList = new ArrayList<>();
    for (BoardContent boardContent : board.getContents()) {
      CardDto cardDto = new CardDto(boardContent.getTitle(), boardContent.getImgUrl(), boardContent.getContent());
      cardDtoList.add(cardDto);
    }

    List<CategoryResponse> categories = new ArrayList<>();
    for(BoardCategory boardCategory: board.getBoardCategories()) {
      Category category = boardCategory.getCategory();
      CategoryResponse cr = CategoryResponse.builder()
        .name(category.getName())
        .pk(category.getPk())
        .build();
      categories.add(cr);
    }

    Users writer = board.getUser();

    BoardResponse boardResponse = BoardResponse.builder()
      .title(board.getTitle())
      .writerPk(writer.getPk())
      .writerName(writer.getNickname())
      .updateAt(board.getUpdateAt())
      .cardCnt(cardDtoList.size())
      .cards(cardDtoList)
      .categories(categories)
      .sources(sourceList)
      .tags(tagList)
      .likeCnt(board.getLikeCnt())
      .bookmarkCnt(board.getBookmarkCnt())
      .build();

    return boardResponse;
  }

  public BoardListResponse getApprovedBoardList(BoardListRequest request) {
    return getBoardList(request, BoardStatus.APPROVED);
  }

  public BoardListResponse getWaitingBoardList(BoardListRequest request) {
    return getBoardList(request, BoardStatus.WAITING);
  }

  //APPROVED된 것만 조회가능
  public BoardListResponse getBoardList(BoardListRequest request, BoardStatus boardStatus) {
    //  private Long categoryPk;
    //  private String search;

    //pageable객체 만들기
    Pageable pageable = makePageable(request.getSortType(), request.getPage(), request.getPageSize());

    List<Board> list;

    //선택한 카테고리, 검색어가 존재한다면 해당 카테고리에 해당하는 검색어와 일치하는 글을 조회
    if (request.getCategoryPk() != null && request.getSearch() != null) {
//      Category category = categoryRepository.findById(request.getCategoryPk())
//        .orElseThrow(() -> new NoSuchElementException("해당하는 카테고리가 없습니다"));
//      list = boardRepository.findByCategoryAndSearch(category, request.getSearch());
      throw new NoSuchElementException("에러");
    } else if (request.getCategoryPk() != null) { //카테고리만 지정된 경우
      throw new NoSuchElementException("에러");
    } else if (request.getSearch() != null) { //검색어만 지정된 경우
      throw new NoSuchElementException("에러");
    } else { //아무것도 지정 x -> 그냥 줌
      list = boardRepository.findByStatus(pageable, boardStatus).getContent();
    }

    List<BriefBoardInfo> briefBoardInfoList = convertToBriefDto(list);

    BoardListResponse boardListResponse = BoardListResponse.builder()
      .list(briefBoardInfoList)
      .cnt(briefBoardInfoList.size())
      .build();

    return boardListResponse;
  }

  private List<BriefBoardInfo> convertToBriefDto(List<Board> list) {
    List<BriefBoardInfo> breifList = new ArrayList<>();

    for (Board b: list) {
      List<Tag> byBoard = tagRepository.findTagByBoard(b)
        .orElseGet(() -> new ArrayList<>());

      BriefBoardInfo brbi = BriefBoardInfo.builder()
        .boardPk(b.getPk())
        .title(b.getTitle())
        .thumbnail(b.getThumbnailUrl())
        .updateAt(b.getUpdateAt())
        .introduction(b.getIntroduction())
        .tag(byBoard.stream()
          .map(Tag::getName)
          .toArray(String[]::new)
        )
        .likeCnt(b.getLikeCnt())
        .bookmarkCnt(b.getBookmarkCnt())
        .build();

      breifList.add(brbi);
    }

    return breifList;
  }

  private Pageable makePageable(SortType sortType, Integer page, Integer pageSize) {

    Sort sort;
    if (sortType == null || sortType == SortType.NEW)
      sort = Sort.by(Sort.Direction.DESC, "likeCnt");
    else
      sort = Sort.by(Sort.Direction.DESC, "updateAt");

    if (page == null)
      page = 1;

    if (pageSize == null)
      pageSize = 10;

    return PageRequest.of(page-1, pageSize, sort);
  }
}
