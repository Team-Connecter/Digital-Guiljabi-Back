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
public class EditRequestDetailResponse {
    private Long boardPk;
    private String title;
    private Long writerPk;
    private String writerName;
    private Long reqUserPk;
    private String reqUserName;
    private LocalDateTime requestTime;
    private String content;
}
