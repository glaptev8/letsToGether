package com.letstogether.authentication.service;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
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
  @Value("${upload.path}")
  private String uploadPath;

  @Override
  public Mono<User> saveUser(User user, FilePart avatar) {
    log.info("saving user {}", user);
    return savePhoto(avatar)
      .flatMap(pathToFile -> {
        user.setPathToAvatar(pathToFile);
        return Mono.empty();
      })
      .then(userRepository.existsByEmailOrPhone(user.getEmail(), user.getPhone())
              .flatMap(exists -> {
                if (exists) {
                  return Mono.error(new UserExistsException(
                    messageSourceService.logMessage("user.exits.code"),
                    messageSourceService.logMessage("user.exits.message"))
                  );
                }
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                return userRepository.save(user);
              })
              .doOnNext(savedUser -> log.info("user was saved: {}", savedUser)));
  }

  @Override
  public Mono<TokenDetails> login(User user) {
    return securityService.authentication(user.getEmail(), user.getPassword());
  }

  @Override
  public Mono<User> updateUser(User user) {
    return userRepository.save(user);
  }

  private Mono<String> savePhoto(FilePart avatar) {
    log.info("saving photo {}", avatar);
    if (avatar == null) {
      return Mono.empty();
    }
    var pathToFile = new StringBuilder();
    var newAvatarName = UUID.randomUUID() + "_" + avatar.filename();
    pathToFile.append(uploadPath).append(newAvatarName);
    return avatar
      .transferTo(new File(pathToFile.toString()))
      .then(Mono.just(newAvatarName))
      .doOnError(e -> log.info("saving file error {0}", e));
  }
}
