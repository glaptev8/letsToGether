package com.letstogether.authentication.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.letstogether.authentication.entity.User;
import com.letstogether.authentication.mapper.MapStructMapper;
import com.letstogether.authentication.security.CustomPrincipal;
import com.letstogether.authentication.security.TokenDetails;
import com.letstogether.authentication.service.UserService;
import com.letstogether.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final MapStructMapper mapper;

  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<UserDto> saveUser(@ModelAttribute User user, @RequestPart(value = "avatar", required = false) FilePart avatar) {
    log.info("saving Controller");
    return userService.saveUser(user, avatar)
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

  @GetMapping("/user/{userId}")
  public Mono<UserDto> getUser(@PathVariable Long userId) {
    return userService.getUserById(userId)
      .map(mapper::fromUser);
  }

  @GetMapping("/user/{userId}/avatar")
  public Mono<UserDto> getUserAvatar(@PathVariable Long userId) {
    return userService.getUserAvatar(userId)
      .map(mapper::fromUser);
  }

  @PostMapping("/users/avatar")
  public Flux<UserDto> getUsersAvatar(@RequestBody List<Long> ids) {
    return userService.getUsersAvatar(ids)
      .map(mapper::fromUser);
  }

  @PostMapping
  public Mono<Long> auth(Authentication authentication) {
    var principal = (CustomPrincipal)authentication.getPrincipal();
    return Mono.just(principal.id());
  }
}
