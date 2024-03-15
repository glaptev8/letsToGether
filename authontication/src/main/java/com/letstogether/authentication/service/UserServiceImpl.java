package com.letstogether.authentication.service;

import java.util.Arrays;
import java.util.Comparator;

import org.springframework.stereotype.Service;

import com.letstogether.authentication.entity.User;
import com.letstogether.authentication.exception.UserExistsException;
import com.letstogether.authentication.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UsersRepository usersRepository;

  @Override
  public Mono<User> saveUser(User user) {
    String s = "saasad";
    //Character;
    s.toCharArray();
    int[] aa = new int[2];
    Arrays.sort();
    Integer.compare(1, 5);
    Comparator<> = (e1, e2) -> {
      return Integer.compare(e1, e2);
    }
    return usersRepository.existsByEmailOrPhone(user.email(), user.phone())
      .flatMap(exists -> {
        if (exists) {
          return Mono.error(new UserExistsException("USER_EXISTS", "user with this phone or email already exists"));
        }
        return usersRepository.save(user);
      });
  }
}
