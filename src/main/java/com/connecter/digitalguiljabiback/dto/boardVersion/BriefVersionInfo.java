package com.connecter.digitalguiljabiback.dto.boardVersion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BriefVersionInfo {
  private Long boardVersionPk;
  private LocalDateTime createAt;
}
