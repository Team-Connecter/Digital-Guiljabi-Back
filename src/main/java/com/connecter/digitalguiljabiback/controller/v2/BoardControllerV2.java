//package com.connecter.digitalguiljabiback.controller.v2;
//
//import com.connecter.digitalguiljabiback.domain.Users;
//import com.connecter.digitalguiljabiback.dto.board.request.AddBoardRequest;
//import com.connecter.digitalguiljabiback.exception.ForbiddenException;
//import com.connecter.digitalguiljabiback.service.BoardService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.NoSuchElementException;
//
//@Tag(name = "BoardControllerV2", description = "정보글 관련 API 버전2")
//@RequiredArgsConstructor
//@RestController
//@Slf4j
//@RequestMapping("/api/v2")
//public class BoardControllerV2 {
//
//  private final BoardService boardService;
//
//  @Operation(summary = "정보글 수정", description = """
//    [로그인 필요] 히스토리가 추가되는 수정기능<br>
//    201: 성공<br>
//    403: 권한없음
//    """)
//  @PatchMapping("/boards/{boardPk}")
//  public ResponseEntity editBoard(
//    @AuthenticationPrincipal Users user,
//    @PathVariable Long boardPk,
//    @RequestBody AddBoardRequest addBoardRequest
//  ) throws NoSuchElementException, ForbiddenException {
//
//    boardService.editBoardV2(user, boardPk, addBoardRequest);
//
//    return ResponseEntity.ok().build();
//  }
//}