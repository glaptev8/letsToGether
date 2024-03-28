package com.letstogether.authentication.exception;

import com.letstogether.exception.ApiException;

public class UserExistsException extends ApiException {

  public UserExistsException(String code, String message) {
    super(code, message);
  }
}
