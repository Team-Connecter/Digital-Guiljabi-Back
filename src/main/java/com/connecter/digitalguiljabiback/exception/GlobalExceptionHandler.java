package com.connecter.digitalguiljabiback.exception;

import com.connecter.digitalguiljabiback.exception.category.CategoryNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(UsernameDuplicatedException.class)
  protected ResponseEntity handlerUsernameDuplicatedException(UsernameDuplicatedException e) {
    log.info("UsernameDuplicatedException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto(e.getMessage()));
  }

  @ExceptionHandler(UserLockedException.class)
  protected ResponseEntity handlerUserLockedException(UserLockedException e) {
    log.info("UserLockedException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDto(e.getMessage()));
  }
  @ExceptionHandler(TokenInValidException.class)
  protected ResponseEntity handlerTokenInValidException(TokenInValidException e) {
    log.info("TokenInValidException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @ExceptionHandler(NoSuchElementException.class)
  protected ResponseEntity handlerNoSuchElementException(NoSuchElementException e) {
    log.info("NoSuchElementException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  protected ResponseEntity handlerCategoryNotFoundException(CategoryNotFoundException e) {
    log.info("CategoryNotFoundException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @ExceptionHandler(ForbiddenException.class)
  protected ResponseEntity handlerForbiddenException(ForbiddenException e) {
    log.info("ForbiddenException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDto(e.getMessage()));
  }

  @ExceptionHandler(CategoryNameDuplicatedException.class)
  protected ResponseEntity handlerCategoryNameDuplicatedException(CategoryNameDuplicatedException e) {
    log.info("CategoryNameDuplicatedException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @ExceptionHandler(ReportDuplicatedException.class)
  protected ResponseEntity handlerReportDuplicatedException(ReportDuplicatedException e) {
    log.info("ReportDuplicatedException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }
  
  @ExceptionHandler(InternalServerException.class)
  protected ResponseEntity handlerInternalServerException(InternalServerException e) {
    log.info("InternalServerException = {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
