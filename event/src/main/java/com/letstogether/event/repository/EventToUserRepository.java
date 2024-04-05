package com.letstogether.event.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.letstogether.event.entity.EventToUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventToUserRepository extends R2dbcRepository<EventToUser, Long> {
  Mono<EventToUser> findByEventIdAndUserId(Long eventId, Long userId);


  Mono<Boolean> existsByEventIdAndUserId(Long eventId, Long userId);

  Mono<Integer> countByEventIdAndSubscribed(Long eventId, Boolean subscribed);

  Flux<EventToUser> findAllByEventIdAndSubscribed(Long eventId, Boolean subscribed);

  @Query("select u.event_id from event_to_user u where u.user_id=:userId and subscribed=true")
  Flux<Long> findAllByUserIdAndSubscribed(Long userId);
}
