package com.letstogether.event.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letstogether.dto.EventDto;
import com.letstogether.dto.EventFilterDto;
import com.letstogether.dto.EventReviewDto;
import com.letstogether.dto.FindReviewRequestDto;
import com.letstogether.dto.ReviewDto;
import com.letstogether.dto.UpdateStatusRequestDto;
import com.letstogether.event.entity.Event;
import com.letstogether.event.entity.EventToUser;
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
  public Mono<EventDto> create(@RequestBody Event event, @RequestHeader("X-USER-ID") Long userId) {
    event.setCreatorId(userId);
    return eventService.save(event)
      .map(mapper::toDto);
  }

  @GetMapping
  public Flux<EventDto> getEvents(@RequestHeader("X-USER-ID") Long userId) {
    return eventService.getAll(userId)
      .map(mapper::toDto);
  }

  @GetMapping("/{id}")
  public Mono<EventDto> getEvent(@PathVariable Long id) {
    return eventService.getEvent(id)
      .map(mapper::toDto);
  }

  @PostMapping("/subscribe")
  public Mono<Boolean> subscribe(@RequestHeader("X-USER-ID") Long userId,
                                 @RequestParam(name = "eventId") Long eventId) {
    return eventService.subscribe(userId, eventId);
  }

  @PostMapping("/unsubscribe")
  public Mono<Boolean> unsubscribe(@RequestHeader("X-USER-ID") Long userId,
                                   @RequestParam(name = "eventId") Long eventId) {
    return eventService.unsubscribe(eventId, userId);
  }

  @PostMapping("/byuser")
  public Flux<EventDto> getEventsByUserId(@RequestHeader("X-USER-ID") Long ownUserId,
                                          @RequestParam(name = "eventId", required = false) Long userId) {
    return eventService.getUsersEventsByUserId(userId == null ? ownUserId : userId)
      .map(mapper::toDto);
  }

  @GetMapping("/bycreator")
  public Flux<EventDto> getEventsByCreator(@RequestHeader("X-USER-ID") Long ownUserId,
                                           @RequestParam(name = "userId", required = false) Long userId) {
    return eventService.getEventsByCreator(userId == null ? ownUserId : userId)
      .map(mapper::toDto);
  }

  @GetMapping("/users/event/{eventId}")
  public Flux<EventToUser> getUsersByEvent(@PathVariable Long eventId) {
    return eventService.getUsersByEvent(eventId);
  }

  @PostMapping("/byfilter")
  public Flux<EventDto> byFilter(@RequestBody EventFilterDto filter, @RequestHeader("X-USER-ID") Long userId) {
    Long id = filter.getUserId() != null ? filter.getUserId() : userId;
    return eventService.getEventsByFilter(filter, id)
      .map(mapper::toDto);
  }

  @PostMapping("/updatestatus")
  public Mono<EventDto> updateStatus(@RequestHeader(value = "X-USER-ID", required = false) Long userId,
                                     @RequestBody UpdateStatusRequestDto updateStatusRequestDto) {
    return eventService
      .updateStatus(updateStatusRequestDto.getEventId(), userId, updateStatusRequestDto.getEventStatus())
      .map(mapper::toDto);
  }

  @PostMapping("/review/check")
  public Mono<Boolean> checkReview(@RequestHeader(value = "X-USER-ID") Long userId, @RequestBody ReviewDto reviewDto) {
    return eventService.reviewCheck(userId, reviewDto);
  }

  @PostMapping("/review")
  public Mono<Void> review(@RequestHeader(value = "X-USER-ID") Long userId, @RequestBody ReviewDto reviewDto) {
    return eventService.review(userId, reviewDto);
  }

  @PostMapping("/all/review")
  public Flux<EventReviewDto> getReview(@RequestBody FindReviewRequestDto findReviewDto) {
    return eventService.getEventReviews(findReviewDto)
      .map(mapper::eventReviewDto);
  }
}
