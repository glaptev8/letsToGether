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
  public Mono<TokenDetails> login(User user) {
    return securityService.authentication(user);
  }

  @Override
  public Mono<User> updateUser(User user, FilePart avatar) {
    return userRepository.findById(user.getId())
      .flatMap(u -> {
        user.setProviderId(u.getProviderId());
        user.setCreatedAt(u.getCreatedAt());
        return imageServiceImpl.savePhoto(avatar)
          .doOnNext(user::setPathToAvatar)
          .then(userRepository.save(user));
      })
      .flatMap(this::setUserHobbies);
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


  private Mono<User> setUserHobbies(User user) {
    return hobbyRepository.deleteAllByUserId(user.getId())
      .thenMany(Mono.justOrEmpty(user.getHobbies())  // Оборачиваем в Mono для обработки null
                  .flatMapMany(Flux::fromIterable)  // Преобразуем List в Flux
                  .flatMap(hobby -> hobbyRepository.save(Hobby.builder()
                                                           .userId(user.getId())
                                                           .hobby(hobby)
                                                           .build())))
      .then(Mono.just(user))  // Возвращаем пользователя после завершения всех операций
      .onErrorResume(e -> {
        // Логирование или обработка ошибок
        System.out.println("Произошла ошибка при обновлении увлечений пользователя: " + e.getMessage());
        return Mono.error(new RuntimeException("Ошибка при обновлении увлечений пользователя", e));
      });
  }
}
