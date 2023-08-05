package com.connecter.digitalguiljabiback.dto.editRequest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class MyEditRequestResponse {
    private Long boardPk;
    private String title;
    private String thumbnail;
    private LocalDateTime createAt;
    private Long likeCnt;
    private Long bookmarkCnt;
}
