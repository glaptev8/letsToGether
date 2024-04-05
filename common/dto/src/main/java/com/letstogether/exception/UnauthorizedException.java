package com.letstogether.exception;

public class UnauthorizedException extends ApiException {

  public UnauthorizedException(String message) {
    super("WEATHERAPI_UNAUTHORIZED", message);
  }
}
