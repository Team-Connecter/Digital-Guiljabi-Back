package com.connecter.digitalguiljabiback.firebase;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FCMNotificationRequestDTO {
    private Long targetUserId;
    private String title;
    private String body;

    @Builder
    public FCMNotificationRequestDTO(Long targetUserId, String title, String body)
    {
        this.targetUserId = targetUserId;
        this.title = title;
        this.body = body;
    }
}
