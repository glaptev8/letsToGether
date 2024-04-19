package com.letstogether.authentication.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.letstogether.authentication.entity.Hobby;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HobbyRepository extends R2dbcRepository<Hobby, Long> {
  @Query("SELECT h.hobby FROM hobby h WHERE user_id = :userId")
  Flux<String> findHobbyByUserId(Long userId);

  Mono<Void> deleteAllByUserId(Long userId);
}
