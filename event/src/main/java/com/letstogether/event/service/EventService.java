package com.letstogether.event.service;

import com.letstogether.dto.EventFilterDto;
import com.letstogether.dto.EventStatus;
import com.letstogether.event.entity.Event;
import com.letstogether.event.entity.EventToUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventService {
  Mono<Event> save(Event event);

  Mono<Boolean> subscribe(Long userId, Long eventId);
  Flux<Event> getAll(Long creatorId);

  Mono<Boolean> unsubscribe(Long eventId, Long userId);

  Flux<Event> getEventsByCreator(Long userId);

  Flux<EventToUser> getUsersByEvent(Long eventId);

  Flux<Event> getUsersEventsByUserId(Long userId);

  Mono<Event> getEvent(Long eventId);

  Flux<Event> getEventsByFilter(EventFilterDto filter, Long userId);
//  Mono<Event> update(Event event);

  Mono<Event> updateStatus(Long eventId, Long userId, EventStatus newEventStatus);
}
