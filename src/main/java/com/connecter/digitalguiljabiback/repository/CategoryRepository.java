package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  String findAncestors = """
    select * from category c where not exists(
        select 1 from clo_category cc where cc.descendant = c.pk and cc.ancestor != c.pk
      );
    """;

  @Procedure(procedureName = "insertCategory")
  void addConnect(@Param("cur_idx") Long categoryPk, @Param("parent_idx")Long parentCategoryPk);

  @Procedure(procedureName = "CdeleteCategory")
  void deleteConnect(@Param("cur_idx") Long categoryPk);

  @Procedure(name = "updateCategory")
  void updateConnect(@Param("cur_idx") Long categoryPk, @Param("parent_idx") Long parentCategoryPk);

  @Query(value = findAncestors, nativeQuery = true)
  Optional<List<Category>> findFirstAncestor();
}
