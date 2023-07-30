package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.BoardListResponse;
import com.connecter.digitalguiljabiback.dto.board.BoardResponse;
import com.connecter.digitalguiljabiback.dto.board.BoardListRequest;
import com.connecter.digitalguiljabiback.service.BoardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BoardController", description = "정보글 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class BoardController {

  private final BoardService boardService;

  //board 만들기
  @PostMapping("/boards")
  public ResponseEntity makeBoard(@AuthenticationPrincipal Users user, @RequestBody AddBoardRequest addBoardRequest) {
    boardService.makeBoard(user, addBoardRequest);

    return ResponseEntity.ok().build();
  }

  //board 상세보기
  @GetMapping("/boards/{boardPk}")
  public ResponseEntity<BoardResponse> getBoard(@PathVariable Long boardPk) {
    BoardResponse boardInfo = boardService.getBoardInfo(boardPk);

    return ResponseEntity.ok(boardInfo);
  }

  //승인된 board 목록 조회 (검색, 카테고리별 확인~)  - @TODO 지금은 모두 조회하는 것, 나중에 sort랑 다 처리 ㄱㄱ
  @GetMapping("/boards")
  public ResponseEntity<BoardListResponse> getApprevedBoardList(@Valid @ModelAttribute BoardListRequest listBoardRequest) {
    BoardListResponse boardList = boardService.getApprovedBoardList(listBoardRequest);

    return ResponseEntity.ok(boardList);
  }

  //ADMIN기능 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

  //board 인증, 카테고리 추기
  
  
  //승인되지 않은 글 모두 조회
  @GetMapping("/admin/boards/waiting")
  public ResponseEntity<BoardListResponse> getWaitingBoardList(@Valid @ModelAttribute BoardListRequest listBoardRequest) {
    BoardListResponse boardList = boardService.getWaitingBoardList(listBoardRequest);

    return ResponseEntity.ok(boardList);
  }
  
  //






}
