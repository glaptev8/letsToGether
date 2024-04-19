package com.letstogether.authentication.controller;

import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.letstogether.authentication.entity.User;
import com.letstogether.authentication.mapper.MapStructMapper;
import com.letstogether.authentication.security.CustomPrincipal;
import com.letstogether.authentication.security.SecurityService;
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
  private final SecurityService securityService;
  private final ReactiveClientRegistrationRepository clientRegistrationRepository;
  private final ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

  @GetMapping("/login/google")
  public Mono<Void> redirectToGoogle(ServerWebExchange exchange) {
    return Mono.defer(() -> {
      String redirectUrl = "/oauth2/authorization/google";  // Стандартный Spring Security endpoint для OAuth2
      exchange.getResponse().setStatusCode(HttpStatus.FOUND);
      exchange.getResponse().getHeaders().setLocation(URI.create(redirectUrl));
      return exchange.getResponse().setComplete();
    });
  }

  @GetMapping("/login")
  public Mono<TokenDetails> log(@AuthenticationPrincipal Mono<OAuth2User> oauth2User) {
    return oauth2User
      .flatMap(oauthUser -> userService.login(User.builder()
                                 .firstName(oauthUser.getAttribute("given_name"))
                                 .lastName(oauthUser.getAttribute("family_name"))
                                 .email(oauthUser.getAttribute("email"))
                                 .providerId(oauthUser.getAttribute("sub"))
                                 .build()));
  }

  @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<UserDto> updateUser(@ModelAttribute User user, @RequestPart(value = "avatar", required = false) FilePart avatar, @RequestHeader("X-USER-ID") Long userId) {
    user.setId(userId);
    return userService.updateUser(user, avatar)
      .map(mapper::fromUser);
  }

  @GetMapping("/user/{userId}")
  public Mono<UserDto> getUser(@PathVariable Long userId) {
    return userService.getUserById(userId)
      .map(mapper::fromUser);
  }

  @PostMapping("/users")
  public Flux<UserDto> getUsers(@RequestBody UserRequestDto userRequestDto) {
    return userService.getUsersById(userRequestDto.getUsersId())
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
    var principal = (CustomPrincipal) authentication.getPrincipal();
    return Mono.just(principal.id());
  }
}
