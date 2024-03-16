package com.letstogether.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
  protected String code;
  public ApiException(String code, String message) {
    super(message);
    this.code = code;
  }
}
