package com.letstogether.authentication.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.letstogether.authentication.entity.User;
import com.letstogether.authentication.exception.UserExistsException;
import com.letstogether.authentication.repository.UserRepository;
import com.letstogether.authentication.security.SecurityService;
import com.letstogether.authentication.security.TokenDetails;
import com.letstogether.messagesourcestarter.service.MessageSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final SecurityService securityService;
  private final MessageSourceService messageSourceService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Mono<User> saveUser(User user) {
    log.info("saving user {}", user);
    return userRepository.existsByEmailOrPhone(user.getEmail(), user.getPhone())
      .flatMap(exists -> {
        if (exists) {
          return Mono.error(new UserExistsException(
            messageSourceService.logMessage("user.not.exits.code"),
            messageSourceService.logMessage("user.not.exits.message"))
          );
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
      })
      .doOnNext(savedUser -> log.info("user was saved: {}", savedUser));
  }

  @Override
  public Mono<TokenDetails> login(User user) {
    return securityService.authentication(user.getEmail(), user.getPassword());
  }

  @Override
  public Mono<User> updateUser(User user) {
    return userRepository.save(user);
  }
}
