//package com.letstogether.authentication.service;
//
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.Base64;
//import java.util.Objects;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Import;
//import org.springframework.mock.web.MockPart;
//import org.testcontainers.shaded.com.google.common.io.Resources;
//
//import com.letstogether.authentication.config.TestContainerConfig;
//import com.letstogether.authentication.config.TestEnvironmentConfig;
//import com.letstogether.authentication.entity.User;
//import com.letstogether.authentication.exception.AuthException;
//import com.letstogether.authentication.util.JsonReader;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//@SpringBootTest
//@ComponentScan(lazyInit = true)
//@Import(TestEnvironmentConfig.class)
//class UserServiceImplTest extends TestContainerConfig {
//
//  @Autowired
//  private UserService userService;
//
//  @Test
//  void saveUser() throws IOException, URISyntaxException {
//    var user = JsonReader.read("json/user.json", User.class);
//    byte[] bytes = Files.readAllBytes(Path.of(Resources.getResource("avatar/avatar.jpeg").toURI()));
//    var mockPart = new MockFilePart("avatar.jpg", Base64.getEncoder().encodeToString(bytes));
//
//    StepVerifier.create(userService.saveUser(user, mockPart))
//      .expectNextMatches(savedUser -> savedUser != null && savedUser.getPathToAvatar() != null)
//      .verifyComplete();
//  }
//
//  @Test
//  void login() throws URISyntaxException, IOException {
//    var user = JsonReader.read("json/user.json", User.class);
//    var password = user.getPassword();
//    byte[] bytes = Files.readAllBytes(Path.of(Resources.getResource("avatar/avatar.jpeg").toURI()));
//    var mockPart = new MockFilePart("avatar.jpg", Base64.getEncoder().encodeToString(bytes));
//
//    StepVerifier.create(
//      userService.saveUser(user, mockPart)
//        .flatMap(savedUser -> {
//          user.setPassword(password);
//          return userService.login(user);
//        })
//      )
//      .expectNextMatches(Objects::nonNull)
//      .verifyComplete();
//  }
//
//  @Test
//  void loginIncorrect() throws URISyntaxException, IOException {
//    var user = JsonReader.read("json/user.json", User.class);
//    var password = user.getPassword();
//    byte[] bytes = Files.readAllBytes(Path.of(Resources.getResource("avatar/avatar.jpeg").toURI()));
//    var mockPart = new MockFilePart("avatar.jpg", Base64.getEncoder().encodeToString(bytes));
//
//    StepVerifier.create(
//        userService.saveUser(user, mockPart)
//          .flatMap(savedUser -> {
//            user.setPassword(password + "incorrect");
//            return userService.login(user);
//          })
//      )
//      .expectError(AuthException.class)
//      .verify();
//  }
//}