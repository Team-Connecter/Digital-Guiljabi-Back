package com.connecter.digitalguiljabiback.repository.specification;

import com.connecter.digitalguiljabiback.domain.*;
import com.connecter.digitalguiljabiback.domain.board.Board;
import com.connecter.digitalguiljabiback.domain.board.BoardStatus;
import com.connecter.digitalguiljabiback.domain.board.BoardTag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class BoardSpecification {

  public static Specification<Board> hasCategory(Category category) {
    return (board, cq, cb) -> cb.equal(board.join("boardCategories").join("category"), category);
  }

  public static Specification<Board> hasStatus(BoardStatus statue) {
    return (board, cq, cb) -> cb.equal(board.get("status"), statue);
  }

  public static Specification<Board> matchesSearchTerm(String searchTerm) {
    return (board, cq, cb) -> {
      Join<Board, BoardTag> boardTagJoin = board.join("boardTags");
      Join<BoardTag, Tag> tagJoin = boardTagJoin.join("tag");

      Predicate matchingTitle = cb.like(board.get("title"), "%" + searchTerm + "%");
      Predicate matchingIntroduction = cb.like(board.get("introduction"), "%" + searchTerm + "%");
      Predicate matchingTag = cb.like(tagJoin.get("name"), "%" + searchTerm + "%");

      return cb.or(matchingTitle, matchingIntroduction, matchingTag);  // title, introduction, tag이름 중 하나와 일치하는 경우
    };
  }

}
