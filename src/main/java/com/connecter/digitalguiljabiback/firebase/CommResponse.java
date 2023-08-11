package com.connecter.digitalguiljabiback.firebase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommResponse {

    private String cdResult;  // 처리 결과 코드 (예: SUCCESS, FAILED)
    private String msgResult; // 처리 결과 메시지

}
