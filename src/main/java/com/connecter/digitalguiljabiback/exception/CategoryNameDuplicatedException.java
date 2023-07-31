package com.connecter.digitalguiljabiback.exception;


public class CategoryNameDuplicatedException extends RuntimeException {
  public CategoryNameDuplicatedException() {
    super();
  }

  public CategoryNameDuplicatedException(String message) {
    super(message);
  }

  public CategoryNameDuplicatedException(String message, Throwable cause) {
    super(message, cause);
  }

  public CategoryNameDuplicatedException(Throwable cause) {
    super(cause);
  }

  protected CategoryNameDuplicatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }



}

