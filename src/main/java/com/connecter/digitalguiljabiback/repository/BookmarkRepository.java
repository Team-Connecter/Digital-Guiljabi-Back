package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.Bookmark;
import com.connecter.digitalguiljabiback.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserAndBoard(Users user, Board board);

    Page<Bookmark> findByUser(Users user, Pageable pageable);

    boolean existsByUserAndBoard(Users user, Board board);

}
