package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {

  Page<Board> findByStatus(BoardStatus status, Pageable pageable);

  List<Board> findByUser(Users user);

  @Query("SELECT b FROM BoardCategory bc join bc.category c join bc.board b WHERE c = ?1 and b.status = ?2")
  List<Board> findByCategoryAndStatus(Category category, BoardStatus status, Pageable pageable);

  List<Board> findByOrderByReportCntDesc(Pageable pageable);

  List<Board> findByReportCntGreaterThanEqualOrderByReportCnt(int reportCnt, Pageable pageable);
  List<Board> findByReportCntGreaterThanEqualOrderByCreateAtDesc(int reportCnt, Pageable pageable);


  Page<Board> findAll(Specification<Board> spec, Pageable pageable);
}
