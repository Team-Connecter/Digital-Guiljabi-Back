package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.board.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Long> {

  @Query(value = "SELECT * FROM board_category WHERE category_pk IN (:categoryPkList)", nativeQuery = true)
  List<BoardCategory> findByCategoryPkIn(@Param("categoryPkList") List<Long> categoryPkList);
}
