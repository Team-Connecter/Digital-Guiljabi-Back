package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.*;
import com.connecter.digitalguiljabiback.dto.board.request.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.request.ApproveBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.request.BoardListRequest;
import com.connecter.digitalguiljabiback.dto.board.request.RejectRequest;
import com.connecter.digitalguiljabiback.dto.board.response.BoardListResponse;
import com.connecter.digitalguiljabiback.dto.board.response.BoardResponse;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.exception.category.CategoryNotFoundException;
import com.connecter.digitalguiljabiback.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "정보글", description = "정보글 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class BoardController {

  private final BoardService boardService;

  @Operation(summary = "정보글 쓰기", description = """
    [로그인 필요] 정보글이 작성되면 승인대기상태로 감<br>
    201: 성공<br>
    400: 필요한 값을 넣지 않음(모든 값은 not null)<br>
    403: 권한없음
    """)
  @PostMapping("/boards")
  public ResponseEntity makeBoard(@AuthenticationPrincipal Users user, @RequestBody @Valid AddBoardRequest addBoardRequest) {
    boardService.makeBoard(user, addBoardRequest);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "정보글 상세보기", description = """
    [모두 접근가능]<br>
    200: 성공<br>
    404: 해당하는 pk의 정보글이 없음
    """)
  @Parameter(name = "boardPk", description = "정보글의 pk")
  @GetMapping("/boards/{boardPk}")
  public ResponseEntity<BoardResponse> getBoard(@AuthenticationPrincipal Users user, @PathVariable Long boardPk) throws NoSuchElementException {
    BoardResponse boardInfo = boardService.getBoardInfo(boardPk, user);

    return ResponseEntity.ok(boardInfo);
  }

  @Operation(summary = "정보글 목록 조회", description = """
    [모두 접근가능] 승인이 완료된 글만 조회됩니다<br>
    200: 성공
    """)
  @Parameters({
    @Parameter(name = "categoryPk", description = "카테고리의 pk (nullable)"),
    @Parameter(name = "q", description = "검색어 (nullable)"),
    @Parameter(name = "pageSize", description = "페이지 크기(1보다 커야함)"),
    @Parameter(name = "page", description = "페이지(0보다 커야함)"),
    @Parameter(name = "sort", description = "정렬기준(최신, 인기)")
  })
  @GetMapping("/boards")
  public ResponseEntity<BoardListResponse> getApprevedBoardList(
    @RequestParam(required = false) Long categoryPk,
    @RequestParam(required = false) String q,
    @RequestParam(required = false, defaultValue = "10") @Min(value = 2, message = "page 크기는 1보다 커야합니다") int pageSize,
    @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "page는 0보다 커야합니다") int page,
    @RequestParam(required = false, defaultValue = "NEW") SortType sort
  ) throws CategoryNotFoundException {
    BoardListResponse boardList = boardService.getApprovedBoardList(categoryPk, q, page, pageSize, sort);

    return ResponseEntity.ok(boardList);
  }

  @Operation(summary = "내가 쓴 글 조회", description = """
  [로그인 필요] 대기중, 승인완료 등 내가 쓴 글 모두 조회<br>
  200: 성공<br>
  403: 로그인 필요
  """)
  @GetMapping("/boards/my")
  public ResponseEntity<BoardListResponse> getMyBoardList(@AuthenticationPrincipal Users user) {
    BoardListResponse myList = boardService.getMyList(user);

    return ResponseEntity.ok(myList);
  }

  @Operation(summary = "정보글 수정", description = """
  [로그인 필요] 작성자 or 관리자만 정보글을 수정 가능<br>
  null로 들어오면 해당 값은 수정되지 않음<br>
  200: 성공<br>
  403: 수정할 권한 없음<br>
  404: 해당하는 pk의 정보글이 없음
  """)
  @PatchMapping("/boards/{boardPk}")
  public ResponseEntity editBoard(
    @AuthenticationPrincipal Users user,
    @PathVariable Long boardPk,
    @RequestBody AddBoardRequest addBoardRequest
  ) throws NoSuchElementException, ForbiddenException {

    boardService.editBoardV2(user, boardPk, addBoardRequest);

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "정보글 삭제", description = """
  [로그인 필요] 작성자 or 관리자만 정보글을 삭제 가능<br>
  200: 성공<br>
  403: 수정할 권한 없음<br>
  404: 해당하는 pk의 정보글이 없음
  """)
  @DeleteMapping("/boards/{boardPk}")
  public ResponseEntity deleteBoard(@AuthenticationPrincipal Users user, @PathVariable Long boardPk) throws NoSuchElementException, ForbiddenException {
    boardService.deleteBoard(user, boardPk);

    return ResponseEntity.ok().build();
  }

  @Secured("USER")
  @Operation(summary = "정보글 좋아요 추가", description = """
    [로그인 필요]<br>
    200: 성공<br>
    404: boardId 를 pk로 가지는 정보글을 찾을 수 없음
    """)
  @PostMapping("/boards/{boardId}/likes")
  public ResponseEntity addLikeToBoard(@AuthenticationPrincipal Users user, @PathVariable("boardId") Long boardId) {
    boardService.addLikeToBoard(user, boardId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "정보글 좋아요 취소", description = """
    [로그인 필요]<br>
    200: 성공<br>
    404: boardId 를 pk로 가지는 정보글을 찾을 수 없음 or 좋아요를 누른 적이 없음
    """)
  @Secured("USER")
  @DeleteMapping("/boards/{boardId}/likes")
  public ResponseEntity cancelLikeToBoard(@AuthenticationPrincipal Users user, @PathVariable("boardId") Long boardId) {
    boardService.cancelLikeToBoard(user, boardId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "인기 정보글 조회", description = """
    [모두 접근가능] 인기 정보글을 조회합니다.<br>
    200: 성공
    """)
  @GetMapping("/boards/popular")
  public ResponseEntity<BoardListResponse> getBoradSortPopular(
    @RequestParam(required = false, defaultValue = "10")int pageSize,
    @RequestParam(required = false, defaultValue = "1")int page
  ) {
    BoardListResponse boardList = boardService.getPopularBoardList(pageSize, page);

    return ResponseEntity.ok(boardList);
  }

  //ADMIN기능 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

  @Operation(summary = "정보글을 승인하고 카테고리 달기", description = """
    [관리자] 정보글을 승인하고, 필요하다면 카테고리를 추가합니다<br>
    200: 성공<br>
    403: 권한 없음
    """)
  @PostMapping("/admin/boards/{boardPk}/approve")
  public ResponseEntity approveBoard(@PathVariable Long boardPk, @RequestBody ApproveBoardRequest request) throws NoSuchElementException {
    boardService.approve(boardPk, request.getCategoryPkList());

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "정보글 승인거절", description = """
    [관리자]<br>
    200: 성공<br>
    403: 권한 없음
    """)
  @PostMapping("/admin/boards/{boardId}/reject")
  public ResponseEntity rejectBoard(@PathVariable Long boardId, @RequestBody @Valid RejectRequest request) throws NoSuchElementException {
    boardService.reject(boardId, request.getRejReason());

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "승인되지 않은 글 모두 조회", description = """
    [관리자]<br>
    200: 성공<br>
    403: 권한 없음
    """)
  @GetMapping("/admin/boards/waiting")
  public ResponseEntity<BoardListResponse> getWaitingBoardList(
    @RequestParam(required = false) Long categoryPk,
    @RequestParam(required = false) String q,
    @RequestParam(required = false, defaultValue = "10") @Min(value = 2, message = "page 크기는 1보다 커야합니다") Integer pageSize,
    @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "page는 0보다 커야합니다") Integer page,
    @RequestParam(required = false, defaultValue = "NEW") SortType sort
  ) throws CategoryNotFoundException {

    BoardListRequest request = BoardListRequest.makeRequest(categoryPk, q, pageSize, page, sort);
    BoardListResponse boardList = boardService.getWaitingBoardList(request);

    return ResponseEntity.ok(boardList);
  }

}


