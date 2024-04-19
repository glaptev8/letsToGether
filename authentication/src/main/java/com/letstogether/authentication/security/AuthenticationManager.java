package com.letstogether.authentication.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.letstogether.authentication.repository.UserRepository;
import com.letstogether.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

  private final UserRepository userRepository;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
//    return Mono.just(authentication);
    var principal = (CustomPrincipal) authentication.getPrincipal();
    return userRepository
      .findById(principal.id())
      .switchIfEmpty(Mono.error(new UnauthorizedException("User not found")))
      .map(user -> authentication);
  }
}
