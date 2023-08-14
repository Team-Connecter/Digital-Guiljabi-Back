package com.connecter.digitalguiljabiback.firebase;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.FirebaseTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림 전송", description = "알림 전송 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class PushNotificationController {

    @Autowired
    private PushNotificationService pushNotificationService;

    @Operation(summary = "토큰 저장", description = """
    [로그인 필요]<br>
    200: 성공<br>
    """)
    @PostMapping("/notification/token")
    public ResponseEntity saveToken(@AuthenticationPrincipal Users user, @RequestBody FirebaseTokenRequest request) {
        pushNotificationService.saveToken(user, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Test용 - 글쓴이에게 수정요청사항 알림 전송", description = """
    알림 전송<br>
    """)
    @PostMapping("/admin/notification")
    public ResponseEntity sendTokenNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationToToken(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }
    
    

}