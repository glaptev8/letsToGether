package com.letstogether.event.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.letstogether.dto.EventDto;
import com.letstogether.dto.EventFilterDto;
import com.letstogether.event.entity.Event;
import com.letstogether.event.mapper.MapStructMapper;
import com.letstogether.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/event/v1")
@RequiredArgsConstructor
public class EventController {

  private final EventService eventService;
  private final MapStructMapper mapper;

  @PostMapping
  public Mono<EventDto> create(@RequestBody Event event) {
    return eventService.save(event)
      .map(mapper::toDto);
  }

  @GetMapping
  public Flux<EventDto> getEvents() {
    return eventService.getAll()
      .map(mapper::toDto);
  }

  @GetMapping("/{id}")
  public Mono<EventDto> getEvent(@PathVariable Long id) {
    return eventService.getEvent(id)
      .map(mapper::toDto);
  }

  @PostMapping("/byuser/{userId}")
  public Flux<EventDto> getEventsByUserId(@RequestHeader("X-USER-ID") Long ownUserId,
                                          @PathVariable(required = false) Long userId) {
    return eventService.getUsersEventsByUserId(userId == null ? ownUserId : userId)
      .map(mapper::toDto);
  }

  @GetMapping("/users")
  public Flux<EventDto> getUsers(@RequestHeader("X-USER-ID") Long userId) {
    return eventService.getByUserId(userId)
      .map(mapper::toDto);
  }

  @PostMapping("/byfilter")
  public Flux<EventDto> postEvent(@RequestBody EventFilterDto filter) {
    return eventService.getEventsByFilter(filter)
      .map(mapper::toDto);
  }
}
