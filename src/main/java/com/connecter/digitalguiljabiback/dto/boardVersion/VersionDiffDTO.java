package com.connecter.digitalguiljabiback.dto.boardVersion;

import com.connecter.digitalguiljabiback.domain.board.VersionDiff;
import com.connecter.digitalguiljabiback.dto.board.CardDto;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VersionDiffDTO {
  private String title;

  private String thumbnailUrl;

  private String introduction;

  private String sources;

  private String contents;

  private String tags;

  public static VersionDiffDTO convert(VersionDiff versionDiff) {
    return VersionDiffDTO.builder()
      .title(versionDiff.getTitle())
      .thumbnailUrl(versionDiff.getThumbnailUrl())
      .introduction(versionDiff.getIntroduction())
      .sources(versionDiff.getSources())
      .contents(versionDiff.getContents())
      .tags(versionDiff.getTags())
      .build();

  }
}
