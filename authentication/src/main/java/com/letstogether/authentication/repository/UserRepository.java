package com.letstogether.authentication.repository;

import java.util.List;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.letstogether.authentication.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {

  Mono<User> findByEmail(String email);

  Mono<Boolean> existsByEmailOrPhone(String email, String phone);

  @Query("SELECT u.id, u.path_to_avatar from users u where id = :userId")
  Mono<User> findAvatarByUserId(Long userId);

  @Query("SELECT * from users u where id in (:ids)")
  Flux<User> findAvatarsByUsersId(List<Long> ids);
}
