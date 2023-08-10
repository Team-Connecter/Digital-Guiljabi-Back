package com.connecter.digitalguiljabiback.domain.board;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board_version") //board의 이전 버전을 저장하는 테이블
public class BoardVersion {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long pk;

  @ManyToOne
  @JoinColumn(name = "board_pk", referencedColumnName = "pk")
  private Board board;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "version_diff_pk", referencedColumnName = "pk")
  private VersionDiff versionDiff;

  @NotNull
  @Column(name = "create_at")
  private LocalDateTime createAt;

  @NotNull
  private String title;

  @Column(name = "thumbnail_url", length = 99999)
  private String thumbnailUrl = null;

  @Lob
  @Column(length = 999999999)
  private String introduction;

  //출처
  @Column(length = 99999999)
  private String sources;

  private String categories;

  @OneToMany(mappedBy = "boardVersion", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BoardVersionContent> boardVersionContents = new ArrayList<BoardVersionContent>();

  //구분자 \n
  private String tags;

  public static BoardVersion convert(Board board, String categoryString, String tagString) {
    return BoardVersion.builder()
      .board(board)
      .createAt(board.getUpdateAt())
      .title(board.getTitle())
      .thumbnailUrl(board.getThumbnailUrl())
      .introduction(board.getIntroduction())
      .sources(board.getSources())
      .categories(categoryString)
      .tags(tagString)
      .build();
  }

  public void addVersionContents(List<BoardVersionContent> versionContentList) {
    this.boardVersionContents = versionContentList;
  }

  public void addVersionDiff(VersionDiff versionDiff) {
    this.versionDiff = versionDiff;
  }
}
