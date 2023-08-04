package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {

  Page<Board> findByStatus(Pageable pageable, BoardStatus status);

  List<Board> findByUser(Users user);

  @Query("SELECT b FROM BoardCategory bc JOIN bc.category c JOIN bc.board b WHERE c = ?1")
  List<Board> findByCategory(Category category);

  List<Board> findByOrderByReportCntDesc(Pageable pageable);

  List<Board> findByReportCntGreaterThanEqualOrderByReportCnt(int reportCnt, Pageable pageable);
  List<Board> findByReportCntGreaterThanEqualOrderByCreateAtDesc(int reportCnt, Pageable pageable);
}
