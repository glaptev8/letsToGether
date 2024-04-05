package com.letstogether.authentication.service;

import java.util.List;

import org.springframework.http.codec.multipart.FilePart;

import com.letstogether.authentication.entity.User;
import com.letstogether.authentication.security.TokenDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
  Mono<User> saveUser(User user, FilePart avatar);

  Mono<TokenDetails> login(User user);

  Mono<User> updateUser(User user);

  Mono<User> getUserById(Long userId);

  Flux<User> getUsersById(List<Long> userIds);

  Flux<User> getUsersAvatar(List<Long> userIds);

  Mono<User> getUserAvatar(Long users);
}
