package com.letstogether.event.repository;

import java.util.List;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.letstogether.event.entity.EventReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventReviewRepository extends R2dbcRepository<EventReview, Long>  {
  Mono<EventReview> findByEventIdAndUserId(Long eventId, Long userId);
  Flux<EventReview> findAllByEventIdIn(List<Long> eventIds);
}
