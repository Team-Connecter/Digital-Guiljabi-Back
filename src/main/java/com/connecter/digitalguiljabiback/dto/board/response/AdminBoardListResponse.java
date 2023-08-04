package com.connecter.digitalguiljabiback.dto.board.response;

import com.connecter.digitalguiljabiback.dto.board.AdminBriefBoardInfo;
import com.connecter.digitalguiljabiback.dto.board.BriefBoardInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminBoardListResponse {
  private int cnt;
  private List<AdminBriefBoardInfo> list;

}
