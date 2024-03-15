package com.letstogether.authentication.service;

import com.letstogether.authentication.entity.User;
import reactor.core.publisher.Mono;

public interface UserService {

  Mono<User> saveUser(User user);
}
