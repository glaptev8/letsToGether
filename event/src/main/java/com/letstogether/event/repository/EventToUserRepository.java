package com.letstogether.event.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.letstogether.event.entity.EventToUser;
import reactor.core.publisher.Mono;

public interface EventToUserRepository extends R2dbcRepository<EventToUser, Long> {
  Mono<EventToUser> findByEventIdAndUserId(Long eventId, Long userId);

  Mono<Boolean> existsByEventIdAndUserId(Long eventId, Long userId);
}
