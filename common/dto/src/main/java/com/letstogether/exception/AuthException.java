package com.letstogether.exception;

public class AuthException extends ApiException {

  public AuthException(String code, String message) {
    super(code, message);
  }
}
