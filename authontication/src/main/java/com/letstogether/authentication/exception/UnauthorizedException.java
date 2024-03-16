package com.letstogether.authentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.letstogether.exception.ApiException;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends ApiException {

  public UnauthorizedException(String message) {
    super("WEATHERAPI_UNAUTHORIZED", message);
  }
}
