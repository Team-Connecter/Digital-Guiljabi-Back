package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.Bookmark;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.bookmark.BookmarkListResponse;
import com.connecter.digitalguiljabiback.dto.bookmark.BookmarkResponse;
import com.connecter.digitalguiljabiback.exception.NotFoundException;
import com.connecter.digitalguiljabiback.repository.BoardRepository;
import com.connecter.digitalguiljabiback.repository.BookmarkRepository;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BookmarkService {

    public final BookmarkRepository bookmarkRepository;
    public final BoardRepository boardRepository;
    public final UserRepository userRepository;

    public void addBookmark(Users user, Long boardId) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException("해당하는 정보글을 찾을 수 없습니다."));

        Users findUser = userRepository.findById(user.getPk())
                .orElseThrow(() -> new NotFoundException("해당하는 사용자가 없습니다."));

        Bookmark bookmark = Bookmark.makeBookmark(findUser, board);

        // 이전에 북마크 했는지 확인
        boolean isClicked = bookmarkRepository.existsByUserAndBoard(user, board);
        if(!isClicked) {
            bookmarkRepository.save(bookmark);
        }

        board.addBookmarkCnt();

    }

    public void cancelBookmark(Users user, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException("해당하는 정보글을 찾을 수 없습니다."));

        Bookmark bookmark = bookmarkRepository.findByUserAndBoard(user, board)
                .orElseThrow(() -> new NotFoundException("북마크가 존재하지 않습니다."));

        bookmarkRepository.delete(bookmark);
    }

    public BookmarkListResponse getBookmarkList(Users user, Pageable pageable) {

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createAt").descending());

        Page<Bookmark> bookmarks = bookmarkRepository.findByUser(user, pageable);

        List<BookmarkResponse> bookmarkResponses = bookmarks.stream().map(bookmark ->
                BookmarkResponse.builder()
                        .boardPk(bookmark.getBoard().getPk())
                        .title(bookmark.getBoard().getTitle())
                        .thumbnail(bookmark.getBoard().getThumbnailUrl())
                        .createAt(bookmark.getCreateAt())
                        .likeCnt(bookmark.getBoard().getLikeCnt())
                        .bookmarkCnt(bookmark.getBoard().getBookmarkCnt())
                        .build()
        ).toList();

        return new BookmarkListResponse(bookmarkResponses.size(), bookmarkResponses);
    }


}
