package com.connecter.digitalguiljabiback.dto.boardVersion;

import com.connecter.digitalguiljabiback.domain.board.BoardVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class VersionListResponse {
  private int cnt;
  private List<BriefVersionInfo> list;


  public static VersionListResponse convert(List<BoardVersion> boardVersionList) {
    List<BriefVersionInfo> versionInfoList = new ArrayList<>();

    for (BoardVersion bv: boardVersionList)
      versionInfoList.add(new BriefVersionInfo(bv.getPk(), bv.getCreateAt()));

    return new VersionListResponse(versionInfoList.size(), versionInfoList);
  }
}
