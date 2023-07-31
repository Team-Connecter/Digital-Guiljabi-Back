package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.*;
import com.connecter.digitalguiljabiback.dto.board.request.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.request.BoardListRequest;
import com.connecter.digitalguiljabiback.dto.board.request.RejectRequest;
import com.connecter.digitalguiljabiback.dto.board.response.BoardListResponse;
import com.connecter.digitalguiljabiback.dto.board.response.BoardResponse;
import com.connecter.digitalguiljabiback.service.BoardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Tag(name = "BoardController", description = "정보글 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class BoardController {

  private final BoardService boardService;

  //board 만들기
  @PostMapping("/boards")
  public ResponseEntity makeBoard(@AuthenticationPrincipal Users user, @RequestBody @Valid AddBoardRequest addBoardRequest) throws NoSuchElementException {
    boardService.makeBoard(user, addBoardRequest);

    return ResponseEntity.ok().build();
  }

  //board 상세보기
  @GetMapping("/boards/{boardPk}")
  public ResponseEntity<BoardResponse> getBoard(@AuthenticationPrincipal Users user, @PathVariable Long boardPk) throws NoSuchElementException {
    BoardResponse boardInfo = boardService.getBoardInfo(boardPk, user);

    return ResponseEntity.ok(boardInfo);
  }

  //승인된 board 목록 조회 (검색, 카테고리별 확인~)  - @TODO 지금은 모두 조회하는 것, 나중에 sort랑 다 처리 ㄱㄱ
  @GetMapping("/boards")
  public ResponseEntity<BoardListResponse> getApprevedBoardList(@Valid @ModelAttribute BoardListRequest listBoardRequest) {
    BoardListResponse boardList = boardService.getApprovedBoardList(listBoardRequest);

    return ResponseEntity.ok(boardList);
  }

  //내가 쓴 글 모두 조회
  @GetMapping("/boards/my")
  public ResponseEntity<BoardListResponse> getMyBoardList(@AuthenticationPrincipal Users user) {
    BoardListResponse myList = boardService.getMyList(user);

    return ResponseEntity.ok(myList);
  }

  @PatchMapping("/boards/{boardId}")
  public ResponseEntity editBoard(@AuthenticationPrincipal Users user, @PathVariable("boardId") Long boardId, @RequestBody AddBoardRequest addBoardRequest) {
    boardService.editBoard(user, boardId, addBoardRequest);

    return ResponseEntity.ok().build();
  }

  //ADMIN기능 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

  //board 승인하기 + 카테고리 추기
  @PostMapping("/admin/boards/{boardId}/approve")
  public ResponseEntity approveBoard(@PathVariable Long boardId, @RequestBody List<Long> categoryPkList) {
    boardService.approve(boardId, categoryPkList);

    return ResponseEntity.ok().build();
  }

  //board 승인 거절하기
  @PostMapping("/admin/boards/{boardId}/reject")
  public ResponseEntity approveBoard(@PathVariable Long boardId, @RequestBody @Valid RejectRequest request) {
    boardService.reject(boardId, request.getRejReason());

    return ResponseEntity.ok().build();
  }


  //승인되지 않은 글 모두 조회
  @GetMapping("/admin/boards/waiting")
  public ResponseEntity<BoardListResponse> getWaitingBoardList(@Valid @ModelAttribute BoardListRequest listBoardRequest) {
    BoardListResponse boardList = boardService.getWaitingBoardList(listBoardRequest);

    return ResponseEntity.ok(boardList);
  }

  //내가






}