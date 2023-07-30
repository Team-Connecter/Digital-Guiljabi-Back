package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.comment.AddCommentRequest;
import com.connecter.digitalguiljabiback.dto.comment.CommentListResponse;
import com.connecter.digitalguiljabiback.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CommentController", description = "댓글 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")

public class CommentController {

    private final CommentService commentService;

    // 댓글 작성 (회원만)
    @Secured("USER")
    @PostMapping("/boards/{boardPk}/comments")
    public ResponseEntity makeComment(@AuthenticationPrincipal Users user, @PathVariable Long boardPk,
                                      @RequestBody AddCommentRequest addCommentRequest)
    {
        commentService.makeComment(user, boardPk, addCommentRequest);
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제 (작성자)
    @DeleteMapping("/comments/{commentPk}")
    public ResponseEntity deleteComment(@AuthenticationPrincipal Users user, @PathVariable Long commentPk)
    {
        commentService.deleteComment(user, commentPk);
        return ResponseEntity.ok().build();
    }

    // 특정 게시글에 대한 모든 댓글 불러오기 (Any)
    @GetMapping("/boards/{boardPk}/comments")
    public ResponseEntity<CommentListResponse> getCommentList(
            // 최신순
            @RequestParam(value = "size",required = false, defaultValue = "10") int size,
            @RequestParam(value = "page",required = false, defaultValue = "0") int page,
            @PathVariable Long boardPk)
    {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok().body(commentService.getCommentList(pageable, boardPk));

    }
}
