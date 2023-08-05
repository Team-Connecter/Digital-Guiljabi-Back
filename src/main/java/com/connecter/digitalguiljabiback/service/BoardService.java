package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.*;
import com.connecter.digitalguiljabiback.dto.board.*;
import com.connecter.digitalguiljabiback.dto.board.request.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.request.BoardListRequest;
import com.connecter.digitalguiljabiback.dto.board.response.AdminBoardListResponse;
import com.connecter.digitalguiljabiback.dto.board.response.BoardListResponse;
import com.connecter.digitalguiljabiback.dto.board.response.BoardResponse;
import com.connecter.digitalguiljabiback.dto.category.CategoryResponse;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.exception.NotFoundException;
import com.connecter.digitalguiljabiback.exception.category.CategoryNotFoundException;
import com.connecter.digitalguiljabiback.repository.*;
import com.connecter.digitalguiljabiback.repository.specification.BoardSpecification;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

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
  private final BoardLikeRepository boardLikeRepository;

  //source를 구분하는 구분자
  private final String sourceDelim = "\tl\tL\t@ls";

  public Board makeBoard(Users user, AddBoardRequest addBoardRequest) throws NoSuchElementException {
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

    return boardRepository.save(board);
  }

  //출처는 이런 걸로 구분함 ㅎ..
  private String sourceStringListToText(String[] source) {
    return String.join(sourceDelim, source);
  }

  private List<BoardContent> getBoardContent(Board board, CardDto[] cards) {
    List<BoardContent> boardContents = new ArrayList<>();
    List<BoardContent> originBCList = board.getContents();

    for (int i =0; i<cards.length; i++) {
      CardDto card = cards[i];

      if (originBCList.size() < i+1) {
        boardContents.add(
          BoardContent.makeBoardContent(board, card.getSubTitle(), card.getImgUrl(), card.getContent())
        );
      } else {
        //기존꺼 재활용
        BoardContent bc = originBCList.get(i);
        bc.edit(card.getSubTitle(), card.getImgUrl(), card.getContent());
        boardContents.add(bc);
      }
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

  public BoardResponse getBoardInfo(Long boardPk, Users user) throws NoSuchElementException {
    Board board = boardRepository.findById(boardPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 정보글을 찾을 수 없습니다"));

    Users writer = board.getUser();

    //게시글이 승인되지 않았다면 관리자, 작성자만 볼 수 있음
    if (board.getStatus() != BoardStatus.APPROVED) {
      if (user == null || (user.getRole() != UserRole.ADMIN && writer.getPk() != user.getPk())) {
        throw new NoSuchElementException("해당 글을 볼 수 없는 사용자입니다");
      }
    }

    //boardTag -> String tag list
    List<String> tagList = board.getBoardTags().stream()
      .map(BoardTag::getTag)
      .map(Tag::getName)
      .collect(Collectors.toList());

    List<String> sourceList = sourceTextToStringList(board.getSources());
    List<CardDto> cardDtoList = CardDto.convert(board.getContents());
    List<CategoryResponse> categories = CategoryResponse.convert(board.getBoardCategories());

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
    return Arrays.asList(sources.split(sourceDelim));
  }

  public BoardListResponse getApprovedBoardList(Long categoryPk, String q, int page,int pageSize, SortType sort) throws CategoryNotFoundException {
    BoardListRequest request = BoardListRequest.builder()
      .categoryPk(categoryPk)
      .q(q)
      .page(page)
      .pageSize(pageSize)
      .sort(sort)
      .build();
    return getBoardList(request, BoardStatus.APPROVED);
  }

  public BoardListResponse getApprovedBoardList(BoardListRequest request) throws CategoryNotFoundException {

    return getBoardList(request, BoardStatus.APPROVED);
  }

  public BoardListResponse getWaitingBoardList(BoardListRequest request) throws CategoryNotFoundException {
    return getBoardList(request, BoardStatus.WAITING);
  }

  //APPROVED된 것만 조회가능
  public BoardListResponse getBoardList(BoardListRequest request, BoardStatus boardStatus) throws CategoryNotFoundException {


    log.info("@@: " + request.getPage() + " " + request.getPageSize() );
    //pageable객체 만들기
    Pageable pageable = makePageable(request.getSort(), request.getPage(), request.getPageSize());

    List<Board> list;

    //선택한 카테고리, 검색어가 존재한다면 해당 카테고리에 해당하는 검색어와 일치하는 글을 조회
    if (request.getCategoryPk() != null && request.getQ() != null) {
      log.info("@@!");
      Category category = categoryRepository.findById(request.getCategoryPk())
        .orElseThrow(() -> new CategoryNotFoundException("해당하는 카테고리가 없습니다"));

      Specification<Board> spec = Specification.where(BoardSpecification.matchesSearchTerm(request.getQ())) //검색어와 매칭
        .and(BoardSpecification.hasCategory(category)) //카테고리 일치
        .and(BoardSpecification.hasStatus(boardStatus)); //해당 status를 가짐

      list = boardRepository.findAll(spec,pageable).getContent();

    } else if (request.getCategoryPk() != null) { //카테고리만 지정된 경우
      log.info("@@2");
      Category category = categoryRepository.findById(request.getCategoryPk())
        .orElseThrow(() -> new CategoryNotFoundException("해당하는 카테고리가 없습니다"));

      list = boardRepository.findByCategoryAndStatus(category, boardStatus, pageable);

    } else if (request.getQ() != null) { //검색어만 지정된 경우
      log.info("@@3");
      Specification<Board> spec = Specification.where(BoardSpecification.matchesSearchTerm(request.getQ())) //검색어와 매칭
        .and(BoardSpecification.hasStatus(boardStatus)); //해당 status를 가짐

      list = boardRepository.findAll(spec, pageable).getContent();

    } else { //아무것도 지정 x -> 그냥 줌
      list = boardRepository.findByStatus(boardStatus, pageable).getContent();
    }

    List<List<Tag>> tagList = new ArrayList<>();
    for (Board b: list) {
      List<Tag> byBoard = tagRepository.findTagByBoard(b)
        .orElseGet(() -> new ArrayList<>());

      log.info("@@@: "+ byBoard);

      tagList.add(byBoard);
    }

    //전체 조회의 경우 태그가 필요함
    List<BriefBoardInfo> briefBoardInfoList = BriefBoardInfo.convertList(list, tagList);

    BoardListResponse boardListResponse = BoardListResponse.builder()
      .list(briefBoardInfoList)
      .cnt(briefBoardInfoList.size())
      .build();

    return boardListResponse;
  }


  private Pageable makePageable(SortType sortType, Integer page, Integer pageSize) throws RuntimeException {

    Sort sort;
    if (sortType == null || sortType == SortType.POP) {
      sort = Sort.by(Sort.Direction.DESC, "likeCnt");
    }
    else {
      sort = Sort.by(Sort.Direction.DESC, "updateAt");
    }


    if (page == null)
      page = 1;

    if (pageSize == null)
      pageSize = 10;

    log.info("@@: " + page + " " + pageSize + " " + sort);

    return PageRequest.of(page-1, pageSize, sort);
  }

  public void approve(Long boardPk, List<Long> categoryPkList) throws NoSuchElementException {
    Board board = boardRepository.findById(boardPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 board가 없습니다"));

    List<Category> categoryList = new ArrayList<>();

    if (categoryPkList != null)
      categoryList = categoryRepository.findByPkIn(categoryPkList);

    //카테고리가 이전과 같지 않거나, 세팅된 적이 없으면
    if (board.getBoardCategories() == null || board.getBoardCategories() != boardCategoryRepository.findByCategoryPkIn(categoryPkList)) {

      //원본 삭제
      if (board.getBoardCategories() != null) {
        for (BoardCategory bc : board.getBoardCategories())
          boardCategoryRepository.deleteById(bc.getPk());
      }

      List<BoardCategory> boardCategoryList = new ArrayList<>();
      for (Category c: categoryList) {
        BoardCategory bc = BoardCategory.makeBoardCategory(c, board);
        boardCategoryList.add(bc);
        boardCategoryRepository.save(bc);
      }

      board.setBoardCategories(boardCategoryList);
    }

    board.approve();
  }

  public void reject(Long boardId, String rejReason) throws NoSuchElementException {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new NoSuchElementException("해당하는 board가 없습니다"));

    board.reject(rejReason);
  }

  public BoardListResponse getMyList(Users user) {
    List<Board> boardList = boardRepository.findByUser(user);

    List<BriefBoardInfo> briefBoardInfoList = BriefBoardInfo.convertList(boardList);

    BoardListResponse boardListResponse = BoardListResponse.builder()
      .list(briefBoardInfoList)
      .cnt(briefBoardInfoList.size())
      .build();

    return boardListResponse;
  }

  public void editBoard(Users user, Long boardId, AddBoardRequest addBoardRequest) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new NoSuchElementException("해당하는 정보글이 없습니다"));

    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new ForbiddenException("유저정보가 이상합니다. 500"));

    //글 작성자거나, admin이 아니라면 수정 불가능
    if (user.getRole() != UserRole.ADMIN && board.getUser() != findUser)
      throw new ForbiddenException("권한이 없는 사용자");

    //source string배열을 올 텍스트로 바꿈
    String sourceText = sourceStringListToText(addBoardRequest.getSources());

    List<BoardTag> boardTags = makeBoardTag(board, addBoardRequest.getTags());
    List<BoardContent> boardContents = getBoardContent(board, addBoardRequest.getCards());

    board.edit(
      addBoardRequest.getTitle(),
      addBoardRequest.getThumbnail(),
      addBoardRequest.getIntroduction(),
      sourceText,
      boardTags,
      boardContents
    );

    boardRepository.save(board);
  }

  public void deleteBoard(Users user, Long boardPk) throws NoSuchElementException, ForbiddenException{
    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new ForbiddenException("user를 찾을 수 없음"));

    Board board = boardRepository.findById(boardPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 pk의 게시판을 찾을 수 없음"));

    if (user.getRole() != UserRole.ADMIN && board.getUser() != findUser) {
      throw new ForbiddenException("권한이 없는 사용자입니다");
    }

    boardRepository.delete(board);
  }

  // 게시글 좋아요
  public void addLikeToBoard(Users user, Long boardId) {

    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new NotFoundException("해당하는 정보글을 찾을 수 없습니다."));

    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new NotFoundException("해당하는 사용자가 없습니다."));

    Likes likes = Likes.makeLikes(findUser, board);

    // 이전에 좋아요를 눌렀는지 확인
    boolean isClicked = boardLikeRepository.existsByUserAndBoard(user, board);
    if(!isClicked) {
      boardLikeRepository.save(likes);
    }

  }

  // 게시글 좋아요 취소
  public void cancelLikeToBoard(Users user, Long boardId) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new NotFoundException("해당하는 정보글을 찾을 수 없습니다."));

    Likes likes = boardLikeRepository.findByUserAndBoard(user, board)
      .orElseThrow(() -> new NotFoundException("좋아요가 존재하지 않습니다."));

    boardLikeRepository.delete(likes);
  }


  public BoardListResponse getPopularBoardList(int pageSize, int page) {
    //pageable객체 만들기
    Pageable pageable = makePageable(SortType.POP, page, pageSize);

    List<Board> list = boardRepository.findAll(pageable).getContent();

    List<List<Tag>> tagList = new ArrayList<>();
    for (Board b: list) {
      List<Tag> byBoard = tagRepository.findTagByBoard(b)
        .orElseGet(() -> new ArrayList<>());

      tagList.add(byBoard);
    }

    //전체 조회의 경우 태그가 필요함
    List<BriefBoardInfo> briefBoardInfoList = BriefBoardInfo.convertList(list, tagList);

    BoardListResponse boardListResponse = BoardListResponse.builder()
      .list(briefBoardInfoList)
      .cnt(briefBoardInfoList.size())
      .build();

    return boardListResponse;
  }
}
