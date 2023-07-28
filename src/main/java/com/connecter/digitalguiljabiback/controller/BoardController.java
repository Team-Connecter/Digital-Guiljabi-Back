package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.AddBoardRequest;
import com.connecter.digitalguiljabiback.service.BoardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
