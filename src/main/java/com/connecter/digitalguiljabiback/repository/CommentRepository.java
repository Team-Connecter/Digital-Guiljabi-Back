package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByBoardPk(Long boardPk, Pageable pageable);

    // 최신순으로 댓글 가져오기
    @Query("SELECT c FROM Comment c WHERE c.board.pk = :boardPk ORDER BY c.createAt DESC")
    Page<Comment> findByBoardPkByOrderByCreateAt(Long boardPk, Pageable pageable);


}
