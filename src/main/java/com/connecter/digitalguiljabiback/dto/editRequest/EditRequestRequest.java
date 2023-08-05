package com.connecter.digitalguiljabiback.dto.editRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditRequestRequest {
    private String content;
}
