package com.connecter.digitalguiljabiback.firebase;

import com.connecter.digitalguiljabiback.domain.FirebaseToken;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.FirebaseTokenRequest;
import com.connecter.digitalguiljabiback.repository.FirebaseTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class PushNotificationService {

    private final FirebaseTokenRepository firebaseTokenRepository;
    private Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    @Autowired
    private final FCMService fcmService;

    @Autowired
    public PushNotificationService(FirebaseTokenRepository firebaseTokenRepository, FCMService fcmService) {
        this.firebaseTokenRepository = firebaseTokenRepository;
        this.fcmService = fcmService;
    }

    //알림 전송
    public void sendPushNotificationToToken(PushNotificationRequest request) {
        try {
            fcmService.sendMessageToToken(request);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    // 토큰 저장
    public void saveToken(Users user, FirebaseTokenRequest request) {

        FirebaseToken firebaseToken = FirebaseToken.makeFirebaseToken(user, request.getToken());
        firebaseTokenRepository.save(firebaseToken);

    }

}