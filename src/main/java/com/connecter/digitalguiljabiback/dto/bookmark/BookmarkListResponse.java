package com.connecter.digitalguiljabiback.dto.bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkListResponse {

    private int cnt;
    private List<BookmarkResponse> bookmarkResponses = new ArrayList<>();

}
