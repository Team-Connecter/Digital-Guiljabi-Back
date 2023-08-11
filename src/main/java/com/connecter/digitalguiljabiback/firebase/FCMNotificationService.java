package com.connecter.digitalguiljabiback.firebase;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FCMNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;

    public String sendNotificationByToken(FCMNotificationRequestDTO requestDTO)
    {
        Optional<Users> user = userRepository.findById(requestDTO.getTargetUserId());

        if(user.isPresent()) {

                Notification notification = Notification.builder()
                        .setTitle(requestDTO.getTitle())
                        .setBody(requestDTO.getBody())
                        .build();

                Message message = Message.builder()
                        //.setToken(user.get().getFirebaseToken())
                        .setNotification(notification)
                        .build();

                try {
                    firebaseMessaging.send(message);
                    return "알림을 성공적으로 전송했습니다. targetUserId: " + requestDTO.getTargetUserId();
                }
                catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                    return "알림 전송을 실패했습니다. targetUserId: " + requestDTO.getTargetUserId();
                }

            }
            else {
                return "서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다. targetUserId: " +
                        requestDTO.getTargetUserId();
            }

        }


    }

