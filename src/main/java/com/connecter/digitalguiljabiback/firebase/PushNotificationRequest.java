package com.connecter.digitalguiljabiback.firebase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushNotificationRequest {
    private String title;
    private String message;
    private String token;

    public PushNotificationRequest() {
        super();
    }

    public PushNotificationRequest(String title, String message, String token) {
        super();
        this.title = title;
        this.message = message;
        this.token = token;
    }

}