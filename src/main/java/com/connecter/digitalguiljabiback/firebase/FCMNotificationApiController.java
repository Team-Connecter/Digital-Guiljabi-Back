package com.connecter.digitalguiljabiback.firebase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "firebase 알림", description = "FCM Notification 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class FCMNotificationApiController {

    private final FCMNotificationService fcmNotificationService;

    @Operation(summary = "알림 전송")
    @PostMapping("/notification")
    public String sendNotificationByToken(@RequestBody FCMNotificationRequestDTO requestDTO)
    {
        return fcmNotificationService.sendNotificationByToken(requestDTO);
    }
}
