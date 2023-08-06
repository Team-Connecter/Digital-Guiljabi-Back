package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.Board;
import com.connecter.digitalguiljabiback.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
  Optional<Tag> findByName(String name);

  @Query("select t from BoardTag bt join bt.board b join bt.tag t where b = ?1")
  Optional<List<Tag>> findTagByBoard(Board b);
}
