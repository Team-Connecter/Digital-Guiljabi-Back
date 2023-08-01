package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.Likes;
import com.connecter.digitalguiljabiback.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserAndBoard(Users user, Board board);
    boolean existsByUserAndBoard(Users user, Board board);

}
