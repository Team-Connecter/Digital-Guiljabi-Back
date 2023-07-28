package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
