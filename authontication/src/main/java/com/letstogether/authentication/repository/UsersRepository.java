package com.letstogether.authentication.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.letstogether.authentication.entity.User;
import reactor.core.publisher.Mono;

public interface UsersRepository extends R2dbcRepository<User, Long> {


  Mono<Boolean> existsByEmailOrPhone(String email, String phone);
}
