package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.*;
import com.connecter.digitalguiljabiback.domain.board.*;
import com.connecter.digitalguiljabiback.dto.board.*;
import com.connecter.digitalguiljabiback.dto.board.request.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.request.ApproveBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.request.BoardListRequest;
import com.connecter.digitalguiljabiback.dto.board.response.BoardListResponse;
import com.connecter.digitalguiljabiback.dto.board.response.BoardResponse;
import com.connecter.digitalguiljabiback.dto.category.CategoryResponse;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.exception.NotFoundException;
import com.connecter.digitalguiljabiback.exception.InternalServerException;
import com.connecter.digitalguiljabiback.exception.category.CategoryNotFoundException;
import com.connecter.digitalguiljabiback.repository.*;
import com.connecter.digitalguiljabiback.repository.board.BoardVersionContentRepository;
import com.connecter.digitalguiljabiback.repository.board.BoardVersionRepository;
import com.connecter.digitalguiljabiback.repository.board.VersionDiffRepository;
import com.connecter.digitalguiljabiback.repository.specification.BoardSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
  private final BoardLikeRepository boardLikeRepository;
  private final BoardVersionContentRepository versionContentRepository;
  private final BoardVersionRepository versionRepository;
  private final VersionDiffRepository versionDiffRepository;
  private final BookmarkRepository bookmarkRepository;

  //source를 구분하는 구분자
  private final String sourceDelim = "\tl\tL\t@ls";

  public Board makeBoard(Users user, AddBoardRequest addBoardRequest){


    //source string배열을 올 텍스트로 바꿈
    String sourceText = sourceListToText(addBoardRequest.getSources());

    Board board = Board.builder()
      .user(user)
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
  private String sourceListToText(List<String> source) {
    if (source == null)
      return null;
    return String.join(sourceDelim, source);
  }

  private List<BoardContent> getBoardContent(Board board, CardDto[] cards) {
    if (cards == null)
      return null;

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

  private List<BoardTag> makeBoardTag(Board board, List<String> tags) {
    if (tags == null)
      return null;
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
    Board board = findBoard(boardPk);
    Users writer = board.getUser();

    //게시글이 승인되지 않았다면 관리자, 작성자만 볼 수 있음
    if (board.getStatus() != BoardStatus.APPROVED) {
      if (user == null || (user.getRole() != UserRole.ADMIN && writer.getPk() != user.getPk())) {
        throw new ForbiddenException("해당 글을 볼 수 없는 사용자입니다");
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

    boolean isBookmarked = false;
    boolean isLiked = false;

    if (user != null) {
      isBookmarked = bookmarkRepository.existsByUserAndBoard(user, board);
      isLiked = boardLikeRepository.existsByUserAndBoard(user, board);
    }

    BoardResponse boardResponse = BoardResponse.builder()
      .title(board.getTitle())
      .introduction(board.getIntroduction())
      .thumbnailUrl(board.getThumbnailUrl())
      .writerPk(writer.getPk())
      .writerName(writer.getNickname())
      .writerProfileUrl(writer.getProfileUrl())
      .updateAt(board.convertDate(board.getUpdateAt()))
      .cardCnt(cardDtoList.size())
      .cards(cardDtoList)
      .categories(categories)
      .sources(sourceList)
      .tags(tagList)
      .likeCnt(board.getLikeCnt())
      .bookmarkCnt(board.getBookmarkCnt())
      .isMine((user != null)? user.getPk() == writer.getPk(): false)
      .isBookmarked(isBookmarked)
      .isLiked(isLiked)
      .isCertified(board.isCertified())
      .build();

    return boardResponse;
  }

  private List<String> sourceTextToStringList(String sources) {
    return Arrays.asList(sources.split(sourceDelim));
  }

  public BoardListResponse getApprovedBoardList(Long categoryPk, String q, int page,int pageSize, SortType sort) throws CategoryNotFoundException {
    BoardListRequest request = BoardListRequest.makeRequest(categoryPk, q, pageSize, page, sort);
    return getBoardList(request, BoardStatus.APPROVED);
  }

  public BoardListResponse getApprovedBoardList(BoardListRequest request) throws CategoryNotFoundException {

    return getBoardList(request, BoardStatus.APPROVED);
  }

  public BoardListResponse getWaitingBoardList(BoardListRequest request) throws CategoryNotFoundException {
    return getBoardList(request, BoardStatus.WAITING);
  }

  //APPROVED된 것만 조회가능
  @Transactional(readOnly = true)
  public BoardListResponse getBoardList(BoardListRequest request, BoardStatus boardStatus) throws CategoryNotFoundException {
    //pageable객체 만들기
    Pageable pageable = makePageable(request.getSort(), request.getPage(), request.getPageSize());

    List<Board> list;

    //선택한 카테고리, 검색어가 존재한다면 해당 카테고리에 해당하는 검색어와 일치하는 글을 조회
    if (request.getCategoryPk() != null && request.getQ() != null) {
      Category category = categoryRepository.findById(request.getCategoryPk())
        .orElseThrow(() -> new CategoryNotFoundException("해당하는 카테고리가 없습니다"));

      Specification<Board> spec = Specification.where(BoardSpecification.matchesSearchTerm(request.getQ())) //검색어와 매칭
        .and(BoardSpecification.hasCategory(category)) //카테고리 일치
        .and(BoardSpecification.hasStatus(boardStatus)); //해당 status를 가짐

      list = boardRepository.findAll(spec,pageable).getContent();

    } else if (request.getCategoryPk() != null) { //카테고리만 지정된 경우
      Category category = categoryRepository.findById(request.getCategoryPk())
        .orElseThrow(() -> new CategoryNotFoundException("해당하는 카테고리가 없습니다"));

      list = boardRepository.findByCategoryAndStatus(category, boardStatus, pageable);

    } else if (request.getQ() != null) { //검색어만 지정된 경우
      Specification<Board> spec = Specification.where(BoardSpecification.matchesSearchTerm(request.getQ())) //검색어와 매칭
        .and(BoardSpecification.hasStatus(boardStatus)); //해당 status를 가짐

      list = boardRepository.findAll(spec, pageable).getContent();

    } else { //아무것도 지정 x -> 그냥 줌
      list = boardRepository.findByStatus(boardStatus, pageable).getContent();
    }

    List<List<Tag>> tagList = new ArrayList<>();
    List<Users> userList = new ArrayList<>();
    for (Board b: list) {
      List<Tag> byBoard = tagRepository.findTagByBoard(b)
        .orElseGet(() -> new ArrayList<>());

      tagList.add(byBoard);
      userList.add(b.getUser());
    }

    //전체 조회의 경우 태그가 필요함
    List<BriefBoardInfo> briefBoardInfoList = BriefBoardInfo.convertList(list, tagList, userList);

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

    return PageRequest.of(page-1, pageSize, sort);
  }

  public void approve(Long boardPk, ApproveBoardRequest request) throws NoSuchElementException {
    Board board = findBoard(boardPk);
    List<Long> categoryPkList = request.getCategoryPkList();

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

      if (request.getIsCertified() != null && request.getIsCertified() == true)
        board.certified();
      else
        board.unCertified();

    }

    board.approve();
  }

  public void reject(Long boardPk, String rejReason) throws NoSuchElementException {
    Board board = findBoard(boardPk);
    board.reject(rejReason);
  }

  private Board findBoard(Long boardPk) {
    Board board = boardRepository.findById(boardPk)
      .orElseThrow(() -> new NoSuchElementException("해당하는 정보글을 찾을 수 없습니다"));

    return board;
  }

  @Transactional(readOnly = true)
  public BoardListResponse getMyList(Users user) {
    List<Board> boardList = boardRepository.findByUser(user);

    List<BriefBoardInfo> briefBoardInfoList = BriefBoardInfo.convertList(boardList);

    BoardListResponse boardListResponse = BoardListResponse.builder()
      .list(briefBoardInfoList)
      .cnt(briefBoardInfoList.size())
      .build();

    return boardListResponse;
  }

  //버전 저장 x edit
  public void editBoard(Users user, Long boardId, AddBoardRequest addBoardRequest) {
    Board board = verifyWriterAndfindBoard(user, boardId);

    //source string배열을 올 텍스트로 바꿈
    String sourceText = sourceListToText(addBoardRequest.getSources());

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

  public void editBoardV2(Users user, Long boardId, AddBoardRequest addBoardRequest) {
    Board board = verifyWriterAndfindBoard(user, boardId);

    //변경사항 비교 및 저장 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    String newTitle = addBoardRequest.getTitle();
    String newThumbnail = addBoardRequest.getThumbnail();
    String newIntroduction = addBoardRequest.getIntroduction();
    List<String> newTagList = addBoardRequest.getTags();
    List<String> newSources = addBoardRequest.getSources();

    //변경사항이 없다면 null
    String titleDiff = (newTitle != null)? getDiff(board.getTitle(), newTitle): null;
    String thumbnailDiff = (newThumbnail != null)? getDiff(board.getThumbnailUrl(), newThumbnail): null;
    String introductionDiff = (newIntroduction != null)? getDiff(board.getIntroduction(), newIntroduction): null;

    List<String> oldTagList = board.getBoardTags().stream()
      .map(BoardTag::getTag)
      .map(Tag::getName)
      .collect(Collectors.toList());

    String tagDiff = (newTagList != null)? getDiff(oldTagList, newTagList): null;

    String contentDiff = null;
    if (addBoardRequest.getCards() != null) {
      List<CardDto> cardDtoList = new ArrayList<>();
      for (BoardContent boardContent : board.getContents()) {
        CardDto cardDto = new CardDto(boardContent.getTitle(), boardContent.getImgUrl(), boardContent.getContent());
        cardDtoList.add(cardDto);
      }

      contentDiff = getContentDiff(cardDtoList, List.of(addBoardRequest.getCards()));
    }

    String sourceDiff = (newSources != null)? getDiff(sourceTextToStringList(board.getSources()), newSources) : null;

    if (titleDiff == null && thumbnailDiff == null && introductionDiff == null && sourceDiff == null && contentDiff == null && tagDiff == null) {
      //변경사항 없음
      return;
    }

    VersionDiff versionDiff = VersionDiff.makeVersionDiff(titleDiff, thumbnailDiff, introductionDiff, sourceDiff, contentDiff, tagDiff);
    versionDiffRepository.save(versionDiff);

    //version을 저장 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    String categoryString = getCategoryString(board.getBoardCategories());
    String tagString = getTagString(board.getBoardTags());

    BoardVersion boardVersion = BoardVersion.convert(board, categoryString, tagString);
    versionRepository.save(boardVersion);

    List<BoardVersionContent> versionContentList = new ArrayList<>();
    for (BoardContent bc: board.getContents()) {
      BoardVersionContent bvc = BoardVersionContent.convert(bc, boardVersion);
      versionContentRepository.save(bvc);
      versionContentList.add(bvc);
    }

    boardVersion.addVersionContents(versionContentList);
    boardVersion.addVersionDiff(versionDiff);

    //정보글을 변경 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    String sourceText = sourceListToText(newSources);
    List<BoardTag> boardTags = makeBoardTag(board, newTagList);
    List<BoardContent> boardContents = getBoardContent(board, addBoardRequest.getCards());

    board.edit(newTitle, newThumbnail, newIntroduction, sourceText, boardTags, boardContents);

    boardRepository.save(board);
  }

  private String getCategoryString(List<BoardCategory> boardCategoryList) {
    StringBuilder categoryBuilder = new StringBuilder();
    for(BoardCategory bc: boardCategoryList) {
      categoryBuilder.append(bc.getCategory().getName());
      categoryBuilder.append("\n");
    }
    return categoryBuilder.toString();
  }

  private String getTagString(List<BoardTag> boardTagList) {
    StringBuilder tagBuilder = new StringBuilder();
    for (BoardTag bt: boardTagList) {
      tagBuilder.append(bt.getTag().getName());
      tagBuilder.append("\n");
    }
    return tagBuilder.toString();
  }

  private Board verifyWriterAndfindBoard(Users user, Long boardId) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new NoSuchElementException("해당하는 정보글이 없습니다"));

    //글 작성자거나, admin이 아니라면 수정 불가능
    if (user.getRole() != UserRole.ADMIN && board.getUser() != user)
      throw new ForbiddenException("권한이 없는 사용자");
    return board;
  }

  private String getContentDiff(List<CardDto> oldContents, List<CardDto> newContents) {
    StringBuilder sb = new StringBuilder();
    int i =0; //old
    int j =0; //new
    while (i<oldContents.size() && j < newContents.size()) {
      String oldSubTitle = oldContents.get(i).getSubTitle();
      String newSubTitle = newContents.get(j).getSubTitle();
      sb = compareAndAppendDiff(sb, oldSubTitle, newSubTitle);

      String oldImg = oldContents.get(i).getImgUrl();
      String newImg = newContents.get(j).getImgUrl();
      sb = compareAndAppendDiff(sb, oldImg, newImg);

      List<String> oldContent = List.of(oldContents.get(i).getContent().split("\n"));
      List<String> newContent = List.of(newContents.get(j).getContent().split("\n"));
      String contentDiff = getDiff(oldContent, newContent);
      if (contentDiff != null)
        sb.append(contentDiff);

      i++; j++;
    }

    //oldContent가 남았으면 모두 삭제
    while (i<oldContents.size()) {
      sb = removeContentDiff(sb, newContents.get(i));
      i++;
    }

    //newContent가 남았으면 모두 추가
    while (j<newContents.size()) {
      sb = insertContentDiff(sb, newContents.get(j));
      j++;
    }

    if (sb.length() == 0)
      return null;

    return sb.substring(0, sb.length());
  }

  private StringBuilder removeContentDiff(StringBuilder sb, CardDto cardDto) {
    return modifyContentDiff(sb, cardDto, "-");
  }

  private StringBuilder insertContentDiff(StringBuilder sb, CardDto cardDto) {
    return modifyContentDiff(sb, cardDto, "+");
  }

  private StringBuilder modifyContentDiff(StringBuilder sb, CardDto cardDto, String m) {
    sb.append(m+ " "); sb.append(cardDto.getSubTitle());
    sb.append("\n");
    sb.append(m+ " "); sb.append(cardDto.getImgUrl());
    sb.append("\n");
    sb.append(m+ " "); sb.append(cardDto.getContent());
    sb.append("\n");

    return sb;
  }

  private StringBuilder compareAndAppendDiff(StringBuilder sb, String oldString, String newString) {
    if (!oldString.equals(newString)) {
      sb.append("- "); sb.append(oldString);
      sb.append("\n");
      sb.append("+ "); sb.append(newString);
    }

    return sb;
  }

  private String getDiff(List<String> oldList, List<String> newList) {
    if (newList == null)
      return null;

    StringBuilder sb = new StringBuilder();
    int i =0; //old
    int j =0; //new
    while (i<oldList.size() && j < newList.size()) {
      if (oldList.get(i).equals(newList.get(i))) {
        i++; j++;
        continue;
      }

      int k = i+1; //old
      while (k < oldList.size() && !oldList.get(k).equals(newList.get(j)))
        k++;

      if (k >= oldList.size()) {
        // 추가
        sb.append("+ "); sb.append(newList.get(j));
        sb.append("\n");
        j++;
      } else {
        // k까지 나머지 싹 다 -
        for (int t = i; t<k; t++) {
          sb.append("- "); sb.append(oldList.get(t));
          sb.append("\n");
        }
        j = k+1; i ++;
      }
    }

    while (i<oldList.size()) {
      sb.append("- "); sb.append(oldList.get(i));
      sb.append("\n");
      i++;
    }

    while (j<newList.size()) {
      sb.append("+ "); sb.append(newList.get(j));
      sb.append("\n");
      j++;
    }

    if (sb.length() == 0)
      return null;

    return sb.substring(0, sb.length());
  }

  //- oldString
  //+ newString
  private String getDiff(String oldString, String newString) {
    StringBuilder sb = new StringBuilder();

    if (!oldString.equals(newString)) {
      sb.append("- ");sb.append(oldString);
      sb.append("\n");
      sb.append("+ ");sb.append(newString);
    }

    return (sb.length() == 0)? null : sb.toString();
  }

  public void deleteBoard(Users user, Long boardPk) throws NoSuchElementException, ForbiddenException{
    Board board = findBoard(boardPk);

    if (user.getRole() != UserRole.ADMIN && board.getUser().getPk() != user.getPk())
      throw new ForbiddenException("권한이 없는 사용자입니다");

    boardRepository.delete(board);
    board.deleteLikeCnt();
  }

  // 게시글 좋아요
  public void addLikeToBoard(Users user, Long boardPk) {
    Board board = findBoard(boardPk);

    // 이전에 좋아요를 눌렀는지 확인
    boolean isClicked = boardLikeRepository.existsByUserAndBoard(user, board);
    if(!isClicked) {
      Likes likes = Likes.makeLikes(user, board);
      boardLikeRepository.save(likes);
      board.addLikeCnt();
    }
    
    //좋아요가 이미 존재하면 무시
  }

  // 게시글 좋아요 취소
  public void cancelLikeToBoard(Users user, Long boardPk) {
    Board board = findBoard(boardPk);

    Likes likes = boardLikeRepository.findByUserAndBoard(user, board)
      .orElseGet(() -> null);

    //좋아요가 존재하지 않는다면 무시
    if (likes == null)
      return;

    //이전에 좋아요를 눌렀다면 삭제
    boardLikeRepository.delete(likes);
    board.deleteLikeCnt();
  }

  @Transactional(readOnly = true)
  public BoardListResponse getPopularBoardList(int pageSize, int page) {
    //pageable객체 만들기
    Pageable pageable = makePageable(SortType.POP, page, pageSize);

    List<Board> list = boardRepository.findAll(pageable).getContent();

    List<List<Tag>> tagList = new ArrayList<>();
    List<Users> userList = new ArrayList<>();
    for (Board b: list) {
      List<Tag> byBoard = tagRepository.findTagByBoard(b)
        .orElseGet(() -> new ArrayList<>());

      tagList.add(byBoard);
      userList.add(b.getUser());
    }

    //전체 조회의 경우 태그가 필요함
    List<BriefBoardInfo> briefBoardInfoList = BriefBoardInfo.convertList(list, tagList, userList);

    BoardListResponse boardListResponse = BoardListResponse.builder()
      .list(briefBoardInfoList)
      .cnt(briefBoardInfoList.size())
      .build();

    return boardListResponse;
  }
}
