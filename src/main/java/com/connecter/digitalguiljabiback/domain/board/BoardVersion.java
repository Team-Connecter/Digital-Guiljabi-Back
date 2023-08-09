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

  @Lob
  @Column(length = 999999999)
  private String introduction;

  //출처
  @Column(length = 99999999)
  private String sources;

  private String categories;

  @OneToMany(mappedBy = "boardVersion", cascade = CascadeType.ALL, orphanRemoval = true) //boardcontent를 삭제하지 않아도 삭제됨 @TODO 테스트
  private List<BoardVersionContent> boardVersionContents = new ArrayList<BoardVersionContent>();

  //구분자 \n
  private String tags;

  public void addVersionContents(List<BoardVersionContent> versionContentList) {
    this.boardVersionContents = versionContentList;
  }

  public void addVersionDiff(VersionDiff versionDiff) {
    this.versionDiff = versionDiff;
  }
}
