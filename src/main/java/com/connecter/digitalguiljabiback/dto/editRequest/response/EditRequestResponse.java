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
public class EditRequestResponse {
    private Long editRequestPk;
    private String writerName;
    private String reqUserName;
    private String title;
    private LocalDateTime createAt;
}
