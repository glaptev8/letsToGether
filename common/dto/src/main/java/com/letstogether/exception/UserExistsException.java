package com.letstogether.exception;

public class UserExistsException extends ApiException {

  public UserExistsException(String code, String message) {
    super(code, message);
  }
}
