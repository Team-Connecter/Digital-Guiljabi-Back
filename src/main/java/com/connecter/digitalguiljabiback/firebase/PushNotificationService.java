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

        String token = request.getToken();

        if (!firebaseTokenRepository.existsByToken(token)) // 토큰이 저장되어 있지 않는 기기면(새 기기)
        {
            FirebaseToken firebaseToken = FirebaseToken.makeFirebaseToken(user, token);
            firebaseTokenRepository.save(firebaseToken);
        } 
        else // 토큰이 이미 저장되어있으면
        {
            // 이미 토큰이 저장되어 있는 경우 해당 토큰의 레코드 삭제 후 새로 저장
            FirebaseToken existingToken = firebaseTokenRepository.findByToken(token);

            if (existingToken != null) {
                // 기존 토큰 레코드 삭제
                firebaseTokenRepository.delete(existingToken);

                // 새 기기의 토큰 정보 저장
                FirebaseToken newFirebaseToken = FirebaseToken.makeFirebaseToken(user, token);
                firebaseTokenRepository.save(newFirebaseToken);
            }

        }

    }

}