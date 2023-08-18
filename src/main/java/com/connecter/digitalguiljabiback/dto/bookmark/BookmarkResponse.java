package com.connecter.digitalguiljabiback.dto.bookmark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkResponse {

    private Long boardPk;
    private String title;
    private String thumbnail;
    private String createAt;
    private Long likeCnt;
    private Long bookmarkCnt;

}
