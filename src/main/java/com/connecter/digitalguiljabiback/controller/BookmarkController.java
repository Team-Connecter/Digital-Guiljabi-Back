package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.bookmark.BookmarkListResponse;
import com.connecter.digitalguiljabiback.service.BookmarkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "북마크", description = "북마크 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // 북마크하기 (회원만)
    @Secured("USER")
    @PostMapping("/boards/{boardId}/bookmarks")
    public ResponseEntity addBookmark(@AuthenticationPrincipal Users user, @PathVariable("boardId") Long boardId) {
        bookmarkService.addBookmark(user, boardId);
        return ResponseEntity.ok().build();
    }

    // 북마크 취소 (본인만)
    @Secured("USER")
    @DeleteMapping("/boards/{boardId}/bookmarks")
    public ResponseEntity cancelBookmark(@AuthenticationPrincipal Users user, @PathVariable("boardId") Long boardId) {
        bookmarkService.cancelBookmark(user, boardId);
        return ResponseEntity.ok().build();
    }


    // 내 북마크 조회 (본인만)
    @Secured("USER")
    @GetMapping("/bookmarks")
    public ResponseEntity<BookmarkListResponse> getBookmarkList(
            // 최신순
            @AuthenticationPrincipal Users user,
            @RequestParam(value = "size",required = false, defaultValue = "10") int size,
            @RequestParam(value = "page",required = false, defaultValue = "0") int page)
    {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok().body(bookmarkService.getBookmarkList(user, pageable));

    }

}
