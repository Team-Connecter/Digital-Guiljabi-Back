package com.connecter.digitalguiljabiback.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LikeTest {
  //좋아요 횟수 증가 확인(likecnt 증가 확인)

  //좋아요 취소 (likecnt 감소 확인)
}
