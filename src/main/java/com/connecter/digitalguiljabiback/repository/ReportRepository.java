package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.Report;
import com.connecter.digitalguiljabiback.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

  Optional<Report> findByUserAndBoard(Users user, Board board);

  List<Report> findByUser(Users user);

  @Query(value = "select r.create_at from report r join board b where b.pk = ?1 ORDER BY r.create_at desc LIMIT 1", nativeQuery = true)
  Optional<LocalDateTime> findByRecentReportByBoard(Long boardPk);
}
