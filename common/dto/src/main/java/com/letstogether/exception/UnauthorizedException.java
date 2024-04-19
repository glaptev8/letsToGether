package com.letstogether.exception;

public class UnauthorizedException extends ApiException {

  public UnauthorizedException(String message) {
    super("LETS_TO_GETHER_UNAUTHORIZED", message);
  }
}
