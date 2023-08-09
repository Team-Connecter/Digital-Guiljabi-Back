package com.connecter.digitalguiljabiback.domain.board;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board_version_content")
public class BoardVersionContent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long pk;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="board_version_pk", referencedColumnName = "pk")
  private BoardVersion boardVersion;

  //subtitle은 null이 될 수 있음 -> nullable
  private String title;

  @Column(name = "img_url", length = 99999)
  private String imgUrl;

  @Lob
  @Column(length = 999999999)
  private String content;

  public static BoardVersionContent convert(BoardContent bc, BoardVersion boardVersion) {
    return new BoardVersionContent(null, boardVersion, bc.getTitle(), bc.getImgUrl(), bc.getContent());
  }
}
