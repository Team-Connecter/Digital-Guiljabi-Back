package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "board_category")
public class BoardCategory {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long pk;

  @ManyToOne
  @JoinColumn(name = "category_pk", referencedColumnName = "pk")
  private Category category;

  @ManyToOne
  @JoinColumn(name = "board_pk", referencedColumnName = "pk")
  private Board board;

  public static BoardCategory makeBoardCategory(Category category, Board board) {
    BoardCategory bc =new BoardCategory();
    bc.category = category;
    bc.board = board;
    return bc;
  }
}
