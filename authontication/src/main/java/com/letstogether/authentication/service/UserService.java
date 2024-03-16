package com.letstogether.authentication.service;

import com.letstogether.authentication.entity.User;
import com.letstogether.authentication.security.TokenDetails;
import reactor.core.publisher.Mono;

public interface UserService {
  Mono<User> saveUser(User user);

  Mono<TokenDetails> login(User user);

  Mono<User> updateUser(User user);
}
