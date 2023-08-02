package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.Report;
import com.connecter.digitalguiljabiback.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

  Optional<Report> findByUserAndBoard(Users user, Board board);
}
