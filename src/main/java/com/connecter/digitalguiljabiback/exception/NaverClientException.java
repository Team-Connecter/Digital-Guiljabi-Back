package com.connecter.digitalguiljabiback.exception;

public class NaverClientException extends RuntimeException {

  public NaverClientException(String message) {
    super(message);
  }

  public NaverClientException(String message, Throwable cause) {
    super(message, cause);
  }
}