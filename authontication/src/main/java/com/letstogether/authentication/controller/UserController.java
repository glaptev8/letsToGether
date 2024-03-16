package com.letstogether.authentication.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.letstogether.authentication.entity.User;
import com.letstogether.authentication.mapper.MapStructMapper;
import com.letstogether.authentication.security.CustomPrincipal;
import com.letstogether.authentication.security.TokenDetails;
import com.letstogether.authentication.service.UserService;
import com.letstogether.dto.UserDto;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final MapStructMapper mapper;

  @PostMapping("/register")
  public Mono<UserDto> saveUser(@RequestBody User user) {
    return userService.saveUser(user)
      .map(mapper::fromUser);
  }

  @PostMapping("/login")
  public Mono<TokenDetails> login(@RequestBody User user) {
    return userService.login(user);
  }

  @PostMapping("/update")
  public Mono<UserDto> updateUser(@RequestBody User user) {
    return userService.updateUser(user)
      .map(mapper::fromUser);
  }
}
