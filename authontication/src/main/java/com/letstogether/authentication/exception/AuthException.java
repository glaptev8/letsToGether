package com.letstogether.authentication.exception;

import com.letstogether.exception.ApiException;

public class AuthException extends ApiException {

  public AuthException(String code, String message) {
    super(code, message);
  }
}
