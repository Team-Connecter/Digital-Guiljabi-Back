package com.connecter.digitalguiljabiback.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long commentPk;
    private Long boardPk;
    private String username;
    private String profileUrl;
    private String content;
    private LocalDateTime createAt;
    private boolean isMine;
}
