package com.connecter.digitalguiljabiback.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentListResponse {
    private Integer cnt;
    private List<CommentResponse> comments;
}
