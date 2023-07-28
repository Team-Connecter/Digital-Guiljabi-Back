package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.AddBoardRequest;
import com.connecter.digitalguiljabiback.dto.board.BoardResponse;
import com.connecter.digitalguiljabiback.service.BoardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BoardController", description = "정보글 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/boards")
public class BoardController {

  private final BoardService boardService;

  @PostMapping
  public ResponseEntity makeBoard(@AuthenticationPrincipal Users user, @RequestBody AddBoardRequest addBoardRequest) {
    boardService.makeBoard(user, addBoardRequest);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{boardPk}")
  public ResponseEntity<BoardResponse> getBoard(@RequestParam Long boardPk) {
    BoardResponse boardInfo = boardService.getBoardInfo(boardPk);

    return ResponseEntity.ok(boardInfo);
  }

//  //모두 접근 가능
//  @GetMapping
//  public ResponseEntity getBoardList() {
//    boardService.getList();
//
//    return ResponseEntity.ok().build();
//  }



}
