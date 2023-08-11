package com.connecter.digitalguiljabiback.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class tmpController {
  @GetMapping("/logoutPage")
  public String logoutPage() {
    return "로그아웃 페이지입니다 ㅋ";
  }
}
