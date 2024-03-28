package com.letstogether.event.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.letstogether.dto.EventStatus;
import com.letstogether.event.entity.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepository extends R2dbcRepository<Event, Long> {
  Flux<Event> findAllByCreatorId(Long userId);

  @Query("""
          SELECT e.* from event e
            left join event_to_user eu on e.id = eu.event_id
          where eu.user_id = :userId
  """)
  Flux<Event> findAllUsersEventByUserId(Long userId);

  Mono<Boolean> existsByIdAndStatus(Long id, EventStatus eventStatus);
}
