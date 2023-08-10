package com.connecter.digitalguiljabiback.repository.board;

import com.connecter.digitalguiljabiback.domain.board.Board;
import com.connecter.digitalguiljabiback.domain.board.BoardVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardVersionRepository extends JpaRepository<BoardVersion, Long> {
  List<BoardVersion> findByBoardOrderByCreateAtAsc(Board board);
}
