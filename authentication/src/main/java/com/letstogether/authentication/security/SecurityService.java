package com.letstogether.authentication.security;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.letstogether.authentication.entity.User;
import com.letstogether.authentication.repository.UserRepository;
import com.letstogether.exception.AuthException;
import com.letstogether.messagesourcestarter.service.MessageSourceService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final MessageSourceService messageSourceService;

  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.expiration}")
  private Integer expirationInSeconds;
  @Value("${jwt.issuer}")
  private String issuer;

  private TokenDetails generateToken(User user) {
    var expirationDate = new Date(new Date().getTime() + expirationInSeconds * 1000);
    var createdDate = new Date();
    String token = Jwts.builder()
      .setIssuer(issuer)
      .setClaims(new HashMap<>() {{
        put("email", user.getEmail());
      }})
      .setSubject(user.getId().toString())
      .setIssuedAt(createdDate)
      .setId(UUID.randomUUID().toString())
      .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
      .setExpiration(expirationDate)
      .compact();
    return new TokenDetails(user.getId(), token, createdDate, expirationDate);
  }

  public Mono<TokenDetails> authentication(String email, String password) {
    return userRepository
      .findByEmail(email)
      .flatMap(user -> {
        if (!passwordEncoder.matches(password, user.getPassword())) {
          return Mono.error(new AuthException(messageSourceService.logMessage("invalid.password.code"),
                                              messageSourceService.logMessage("invalid.password.message")));
        }
        return Mono.just(generateToken(user));
      })
      .switchIfEmpty(Mono.error(new AuthException(messageSourceService.logMessage("user.not.found.code"),
                                                  messageSourceService.logMessage("user.not.found.message"))));
  }
}
