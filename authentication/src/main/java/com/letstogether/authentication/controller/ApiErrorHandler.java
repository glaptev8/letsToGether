package com.letstogether.authentication.controller;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letstogether.dto.ErrorResponse;
import com.letstogether.exception.AuthException;
import com.letstogether.exception.UnauthorizedException;
import com.letstogether.exception.UserExistsException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiErrorHandler implements ErrorWebExceptionHandler {

  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    var response = exchange.getResponse();
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    if (ex instanceof UserExistsException e) {
      response.setStatusCode(HttpStatus.BAD_REQUEST);
      log.info(e.getMessage());
      return wrapTransactionResponse(response, e.getCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    if (ex instanceof AuthException e) {
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      log.info(e.getMessage());
      return wrapTransactionResponse(response, e.getCode(), e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    if (ex instanceof UnauthorizedException e) {
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return wrapTransactionResponse(response, e.getCode(), e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    if (ex instanceof ExpiredJwtException e) {
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return wrapTransactionResponse(response, "LETSTOGETHER_UNAUTHORIZED", e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    return null;
  }

  private Mono<Void> wrapTransactionResponse(ServerHttpResponse response,
                                             String errorCode,
                                             String errorMessage,
                                             HttpStatus httpStatus) {
    try {
      return response
        .writeWith(Mono.just(response
                               .bufferFactory()
                               .wrap(objectMapper
                                       .writeValueAsString(new ResponseEntity<>(ErrorResponse.builder()
                                                                                  .code(errorCode)
                                                                                  .message(errorMessage)
                                                                                  .build(),
                                                                                httpStatus))
                                       .getBytes())));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
