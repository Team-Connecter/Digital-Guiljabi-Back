package com.connecter.digitalguiljabiback.domain.board;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "version_diff")
public class VersionDiff {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long pk;

  //- oldTitle
  //+ newTitle
  private String title;

  @Column(name = "thumbnail_url", length = 99999)
  private String thumbnailUrl;

  @Lob
  @Column(length = 999999999)
  private String introduction;

  //출처
  @Column(length = 99999999)
  private String sources;

  @Column(length = 99999999)
  private String contents;

  private String tags;

  public static VersionDiff makeVersionDiff(String titleDiff, String thumbnailDiff, String introductionDiff, String sourceDiff, String contentDiff, String tagDiff) {
    VersionDiff vd = new VersionDiff();
    vd.title = titleDiff;
    vd.thumbnailUrl = thumbnailDiff;
    vd.introduction = introductionDiff;
    vd.sources = sourceDiff;
    vd.contents = contentDiff;
    vd.tags = tagDiff;

    return vd;
  }

}
