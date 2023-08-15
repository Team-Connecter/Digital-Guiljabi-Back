package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.OauthType;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.login.UserRequest;
import com.connecter.digitalguiljabiback.exception.UserLockedException;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {


  @Autowired
  private UserService userService;
  @Autowired private UserRepository userRepository;
  @Autowired private LoginService loginService;

  Users user;
  private final String uid = "KAKAOUidTest";

  @BeforeEach
  void setUp() {
    user = Users.makeUsers(uid, uid, OauthType.KAKAO);
    userRepository.save(user);
  }

  @DisplayName("정지먹이기/풀어주기 테스트")
  @Transactional
  @Order(1)
  @Test
  void lockUser() {
    userService.lockUser(user.getPk());
    assertFalse(user.isAccountNonLocked());

    //oauth 로그인 테스트
    assertThrows(UserLockedException.class, () -> {
      loginService.loginOrCreate(new UserRequest(uid.substring(5)), OauthType.KAKAO, null);
    });


    //임시로그인 테스트
    assertThrows(UserLockedException.class, () -> {
      loginService.tempLogin(new UserRequest(uid), null);
    });

    userService.unlockUser(user.getPk());
    assertTrue(user.isAccountNonLocked());
  }

}