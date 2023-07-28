package com.connecter.digitalguiljabiback.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(UsernameDuplicatedException.class)
  protected ResponseEntity handlerUsernameDuplicatedException(UsernameDuplicatedException e) {
    log.info("UsernameDuplicatedException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }
}
