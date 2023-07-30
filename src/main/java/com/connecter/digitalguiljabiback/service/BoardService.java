package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.*;
import com.connecter.digitalguiljabiback.dto.board.*;
import com.connecter.digitalguiljabiback.dto.board.request.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.request.BoardListRequest;
import com.connecter.digitalguiljabiback.dto.board.response.BoardListResponse;
import com.connecter.digitalguiljabiback.dto.board.response.BoardResponse;
import com.connecter.digitalguiljabiback.dto.category.CategoryResponse;
import com.connecter.digitalguiljabiback.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BoardService {

  private final BoardRepository boardRepository;
  private final TagRepository tagRepository;
  private final CategoryRepository categoryRepository;
  private final BoardCategoryRepository boardCategoryRepository;
  private final UserRepository userRepository;
  private final BoardTagRepository boardTagRepository;

  //source를 구분하는 구분자
  private final String sourceDelim = "\tl\tL\t@ls";

  public void makeBoard(Users user, AddBoardRequest addBoardRequest) throws RuntimeException {
    //굳이 안넣어도 될듯
    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new NoSuchElementException("해당하는 사용자가 없습니다"));

    //source string배열을 올 텍스트로 바꿈
    String sourceText = sourceStringListToText(addBoardRequest.getSources());

    Board board = Board.builder()
      .user(findUser)
      .title(addBoardRequest.getTitle())
      .thumbnailUrl(addBoardRequest.getThumbnail())
      .introduction(addBoardRequest.getIntroduction())
      .sources(sourceText)
      .build();

    List<BoardTag> boardTags = makeBoardTag(board, addBoardRequest.getTags());
    List<BoardContent> boardContents = getBoardContent(board, addBoardRequest.getCards());

    board.setInfo(boardTags, boardContents);

    boardRepository.save(board);
  }

  //출처는 이런 걸로 구분함 ㅎ..
  private String sourceStringListToText(String[] source) {
    return String.join(sourceDelim, source);
  }

  private List<BoardContent> getBoardContent(Board board, CardDto[] cards) {
    List<BoardContent> boardContents = new ArrayList<>();

    for (CardDto card : cards) {
      boardContents.add(
        BoardContent.makeBoardContent(board, card.getSubTitle(), card.getImgUrl(), card.getContent())
      );
    }

    return boardContents;
  }

  private List<BoardTag> makeBoardTag(Board board, String[] tags) {
    List<BoardTag> boardTags = new ArrayList<>();

    for (String tag: tags) {
      Tag findTag = getTag(tag);
      BoardTag boardTag = BoardTag.makeBoardTag(board, findTag);
      boardTags.add(boardTag);
    }

    return boardTags;
  }
  
  private Tag getTag(String tagName) throws RuntimeException {
    Tag findTag = tagRepository.findByName(tagName)
      .orElseGet(() -> null);
    
    //없으면 생성, 있으면 재활용
    if (findTag == null) {
      findTag = Tag.makeTag(tagName);
      findTag = tagRepository.save(findTag);
    }

    return findTag;
  }

  public BoardResponse getBoardInfo(Long boardPk) throws RuntimeException {
    Board board = boardRepository.findById(boardPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 정보글을 찾을 수 없습니다"));

    //boardTag -> String tag list
    List<String> tagList = board.getBoardTags().stream()
      .map(BoardTag::getTag)
      .map(Tag::getName)
      .collect(Collectors.toList());

    List<String> sourceList = sourceTextToStringList(board.getSources());

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

  private List<String> sourceTextToStringList(String sources) {
    List<String> sourceList = new ArrayList<>();
    StringTokenizer st = new StringTokenizer(sources, sourceDelim);
    while (st.hasMoreTokens())
      sourceList.add(st.nextToken());

    return sourceList;
  }

  public BoardListResponse getApprovedBoardList(BoardListRequest request) throws RuntimeException {
    return getBoardList(request, BoardStatus.APPROVED);
  }

  public BoardListResponse getWaitingBoardList(BoardListRequest request) throws RuntimeException {
    return getBoardList(request, BoardStatus.WAITING);
  }

  //APPROVED된 것만 조회가능
  public BoardListResponse getBoardList(BoardListRequest request, BoardStatus boardStatus) throws RuntimeException {
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

  private List<BriefBoardInfo> convertToBriefDto(List<Board> list) throws RuntimeException {
    return convertToBriefDto(list, false);
  }

  private List<BriefBoardInfo> convertToBriefDto(List<Board> list, boolean ismyData) throws RuntimeException {
    List<BriefBoardInfo> breifList = new ArrayList<>();

    for (Board b: list) {
      BriefBoardInfo brbi;

      if (ismyData) { //내 데이터면 -> 태그 필요 x
        brbi= BriefBoardInfo.builder()
          .boardPk(b.getPk())
          .title(b.getTitle())
          .thumbnail(b.getThumbnailUrl())
          .updateAt(b.getUpdateAt())
          .likeCnt(b.getLikeCnt())
          .bookmarkCnt(b.getBookmarkCnt())
          .status(b.getStatus())
          .reason(b.getReason())
          .build();
      } else { //전체 조회면 -> 태그 필요
        List<Tag> byBoard = tagRepository.findTagByBoard(b)
          .orElseGet(() -> new ArrayList<>());

        brbi= BriefBoardInfo.builder()
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
      }

      breifList.add(brbi);
    }

    return breifList;
  }

  private Pageable makePageable(SortType sortType, Integer page, Integer pageSize) throws RuntimeException {

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

  public void approve(Long boardId, List<Long> categoryPkList) throws RuntimeException {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new NoSuchElementException("해당하는 board가 없습니다"));

    List<Category> categoryList = categoryRepository.findByPkIn(categoryPkList);

    //카테고리가 이전과 같지 않거나, 세팅된 적이 없으면
    if (board.getBoardCategories() == null || board.getBoardCategories() != boardCategoryRepository.findByCategoryPkIn(categoryPkList)) {

      //원본 삭제
      if (board.getBoardCategories() != null) {
        for (BoardCategory bc : board.getBoardCategories())
          boardCategoryRepository.deleteById(bc.getPk());
      }

      List<BoardCategory> boardCategoryList = categoryList.stream()
        .map((Category c) -> BoardCategory.makeBoardCategory(c, board))
        .collect(Collectors.toList());

      board.setBoardCategories(boardCategoryList);
    }

    board.approve();
  }

  public void reject(Long boardId, String rejReason) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new NoSuchElementException("해당하는 board가 없습니다"));

    board.reject(rejReason);
  }

  public BoardListResponse getMyList(Users user) {
    List<Board> boardList = boardRepository.findByUser(user);

    List<BriefBoardInfo> briefBoardInfoList = convertToBriefDto(boardList, true);

    BoardListResponse boardListResponse = BoardListResponse.builder()
      .list(briefBoardInfoList)
      .cnt(briefBoardInfoList.size())
      .build();

    return boardListResponse;
  }

}
