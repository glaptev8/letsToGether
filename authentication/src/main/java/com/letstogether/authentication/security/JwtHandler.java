package com.letstogether.authentication.security;

import java.util.Base64;
import java.util.Date;

import com.letstogether.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import reactor.core.publisher.Mono;

public class JwtHandler {

  private final String secret;

  public JwtHandler(String secret) {
    this.secret = secret;
  }

  public Mono<VerificationResult> check(String accessToken) {
    return Mono.just(verify(accessToken))
      .onErrorResume(e -> Mono.error(new UnauthorizedException(e.getMessage())));
  }

  private VerificationResult verify(String token) {
    var claims = getClaimsFromToken(token);
    var expirationDate = claims.getExpiration();

    if (expirationDate.before(new Date())) {
      throw new RuntimeException("token expired");
    }

    return new VerificationResult(claims, token);
  }

  private Claims getClaimsFromToken(String token) {
    try {
      return Jwts.parser()
        .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
        .parseClaimsJws(token)
        .getBody();
    } catch (MalformedJwtException e) {
      throw new UnauthorizedException("incorrect jwt token");
    }
  }

}
