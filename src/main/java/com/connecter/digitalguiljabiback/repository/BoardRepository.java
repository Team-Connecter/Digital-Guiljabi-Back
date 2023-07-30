package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

  Page<Board> findByStatus(Pageable pageable, BoardStatus status);

  List<Board> findByUser(Users user);

//  @Query("SELECT b FROM BoardCategory bc JOIN bc.board b JOIN bc.category c WHERE c=?1 AND ")
//  Optional<List<Board>> findByCategoryAndSearch(Category category, String search);

//  //@TODO
//  Optional<Board> findByCategory();
}
