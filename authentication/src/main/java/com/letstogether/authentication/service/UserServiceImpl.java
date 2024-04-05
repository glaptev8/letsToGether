package com.letstogether.authentication.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.letstogether.authentication.entity.Hobby;
import com.letstogether.authentication.entity.User;
import com.letstogether.authentication.repository.HobbyRepository;
import com.letstogether.authentication.repository.UserRepository;
import com.letstogether.authentication.security.SecurityService;
import com.letstogether.authentication.security.TokenDetails;
import com.letstogether.dto.HobbyType;
import com.letstogether.exception.UserExistsException;
import com.letstogether.messagesourcestarter.service.MessageSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final SecurityService securityService;
  private final MessageSourceService messageSourceService;
  private final PasswordEncoder passwordEncoder;
  private final ImageServiceImpl imageServiceImpl;
  private final HobbyRepository hobbyRepository;

  @Override
  public Mono<User> saveUser(User user, FilePart avatar) {
    log.info("Saving user {}", user);
    return imageServiceImpl.savePhoto(avatar)
      .doOnNext(user::setPathToAvatar)
      .then(saveUser(user))
      .flatMap(this::setUserHobbies)
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

  @Override
  public Mono<User> getUserById(Long userId) {
    return userRepository.findById(userId)
      .flatMap(user -> hobbyRepository.findHobbyByUserId(userId)
        .map(HobbyType::valueOf)
        .collect(Collectors.toSet())
        .doOnNext(user::setHobbies)
        .then(Mono.just(user)))
      .switchIfEmpty(Mono.error(new RuntimeException("User not found"))); // TODO: 2024-03-31 UserNotFoundException
  }

  @Override
  public Flux<User> getUsersById(List<Long> userIds) {
    return userRepository.findAllById(userIds)
      .flatMap(user -> hobbyRepository.findHobbyByUserId(user.getId())
        .map(HobbyType::valueOf)
        .collect(Collectors.toSet())
        .doOnNext(user::setHobbies)
        .then(Mono.just(user)));
  }

  @Override
  public Flux<User> getUsersAvatar(List<Long> userIds) {
    return userRepository.findAvatarsByUsersId(userIds);
  }

  @Override
  public Mono<User> getUserAvatar(Long userId) {
    return userRepository.findAvatarByUserId(userId);
  }

  private Mono<User> saveUser(User user) {
    return userRepository.existsByEmailOrPhone(user.getEmail(), user.getPhone())
      .flatMap(exists -> {
        if (exists) {
          return Mono.error(new UserExistsException(
            messageSourceService.logMessage("user.exits.code"),
            messageSourceService.logMessage("user.exits.message")));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
      });
  }

  private Mono<User> setUserHobbies(User user) {
    user.setHobbies(user.getHobbies());
    return Flux.fromIterable(user.getHobbies())
      .flatMap(hobby -> hobbyRepository.save(Hobby.builder()
                                               .userId(user.getId())
                                               .hobby(hobby)
                                               .build()))
      .collectList()
      .thenReturn(user);
  }
}
