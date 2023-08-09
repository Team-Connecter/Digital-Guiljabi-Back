package com.connecter.digitalguiljabiback.controller.v2;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.board.request.AddBoardRequest;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.service.BoardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Tag(name = "BoardControllerV2", description = "정보글 관련 API 버전2")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v2")
public class BoardControllerV2 {

  private final BoardService boardService;

  //정보글 수정
  @PatchMapping("/boards/{boardPk}")
  public ResponseEntity editBoard(
    @AuthenticationPrincipal Users user,
    @PathVariable Long boardPk,
    @RequestBody AddBoardRequest addBoardRequest
  ) throws NoSuchElementException, ForbiddenException {

    boardService.editBoardV2(user, boardPk, addBoardRequest);

    return ResponseEntity.ok().build();
  }
}