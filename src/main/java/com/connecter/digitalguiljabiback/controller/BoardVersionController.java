package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.dto.boardVersion.VersionDiffDTO;
import com.connecter.digitalguiljabiback.dto.boardVersion.VersionInfo;
import com.connecter.digitalguiljabiback.dto.boardVersion.VersionListResponse;
import com.connecter.digitalguiljabiback.service.BoardVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BoardVersionController", description = "정보글 버전 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class BoardVersionController {

  private final BoardVersionService boardVersionService;

  @Operation(summary = "정보글 히스토리 조회", description = "정보글의 히스토리를 조회합니다")
  @Parameter(name = "boardPk", description = "정보글의 pk")
  @GetMapping("/board/{boardPk}/history")
  public ResponseEntity<VersionListResponse> getBoardVersionList(@PathVariable Long boardPk) {
    VersionListResponse response = boardVersionService.getVersionList(boardPk);

    return ResponseEntity.ok(response);
  }

  @Operation(summary = "히스토리 상세 조회", description = "히스토리를 상세 조회합니다")
  @Parameter(name = "boardVersionPk", description = "버전의 pk")
  @GetMapping("/board-version/{boardVersionPk}")
  public ResponseEntity<VersionInfo> getBoardVersion(@PathVariable Long boardVersionPk) {
    VersionInfo response = boardVersionService.getVersion(boardVersionPk);

    return ResponseEntity.ok(response);
  }

  @Operation(summary = "히스토리 변경내역 조회", description = "이전 버전과 비교해 변경된 내용을 조회합니다")
  @Parameter(name = "boardVersionPk", description = "버전의 pk")
  @GetMapping("/board-version/{boardVersionPk}/diff")
  public ResponseEntity<VersionDiffDTO> getDiff(@PathVariable Long boardVersionPk) {
    VersionDiffDTO response = boardVersionService.getVersionDiff(boardVersionPk);

    return ResponseEntity.ok(response);
  }


}
