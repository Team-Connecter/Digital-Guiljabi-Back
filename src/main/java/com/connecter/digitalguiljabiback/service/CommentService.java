package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.board.Board;
import com.connecter.digitalguiljabiback.domain.Comment;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.comment.AddCommentRequest;
import com.connecter.digitalguiljabiback.dto.comment.CommentListResponse;
import com.connecter.digitalguiljabiback.dto.comment.CommentResponse;
import com.connecter.digitalguiljabiback.exception.ForbiddenException;
import com.connecter.digitalguiljabiback.exception.NotFoundException;
import com.connecter.digitalguiljabiback.repository.BoardRepository;
import com.connecter.digitalguiljabiback.repository.CommentRepository;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.connecter.digitalguiljabiback.domain.UserRole.ADMIN;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    
    // 댓글 작성
    public void makeComment(Users user, Long boardPk, AddCommentRequest addCommentRequest) {
        Board board = boardRepository.findById(boardPk)
                .orElseThrow(() -> new NotFoundException("해당하는 정보글을 찾을 수 없습니다."));

        Users findUser = userRepository.findById(user.getPk())
                .orElseThrow(() -> new NotFoundException("해당하는 사용자가 없습니다."));

        Comment comment = Comment.makeComment(board, findUser, addCommentRequest.getContent());

        commentRepository.save(comment);

    }
    
    // 댓글 삭제
    public void deleteComment(Users user, Long commentPk) {
        Comment comment = commentRepository.findById(commentPk)
                .orElseThrow(() -> new NotFoundException("해당하는 댓글을 찾을 수 없습니다."));

        // Admin은 통과, 회원이면 댓글 확인
        if(!user.getRole().equals(ADMIN) && comment.getUser().getPk() != user.getPk()) {
            throw new ForbiddenException("작성자만 댓글을 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }

    // 댓글 조회
    public CommentListResponse getCommentList(Pageable pageable, Long boardPk) {

        // 게시글 존재 여부 확인
        boardRepository.findById(boardPk)
                .orElseThrow(() -> new NotFoundException("해당하는 정보글을 찾을 수 없습니다."));

        Page<Comment> comments = commentRepository.findByBoardPkByOrderByCreateAt(boardPk, pageable);

        List<CommentResponse> commentResponses = comments.stream().map(comment ->
            CommentResponse.builder()
                    .commentPk(comment.getPk())
                    .boardPk(boardPk)
                    .username(comment.getUser().getUsername())
                    .profileUrl(comment.getUser().getProfileUrl())
                    .content(comment.getContent())
                    .createAt(comment.getCreateAt())
                    .build()
        ).toList();

        return new CommentListResponse(commentResponses.size(), commentResponses);

    }
    
    
}
