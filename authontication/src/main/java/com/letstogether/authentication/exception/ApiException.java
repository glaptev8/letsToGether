package com.letstogether.authentication.exception;

public class ApiException extends RuntimeException {
  private String code;
  public ApiException(String code, String message) {
    super(message);
    this.code = code;
  }
}
