package com.letstogether.event.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.letstogether.dto.ActivityType;
import com.letstogether.dto.EventFilterDto;
import com.letstogether.dto.EventStatus;
import com.letstogether.event.entity.Event;
import com.letstogether.event.entity.EventToUser;
import com.letstogether.event.repository.EventRepository;
import com.letstogether.event.repository.EventToUserRepository;
import com.letstogether.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final EventToUserRepository eventToUserRepository;
  private final TransactionalOperator transactionalOperator;
  private final R2dbcEntityTemplate entityTemplate;

  @Override
  public Mono<Event> save(Event event) {
    log.info("save event: {}", event);
    if ((event.getEndDate() != null && event.getEndDate().isBefore(event.getStartDate()) || event.getStartDate().isBefore(LocalDateTime.now().plusHours(3)))) {
      return Mono.error(new ApiException("EVENT_ERROR","End date is before start date or in the past")); // TODO: 2024-03-27 EventException
    }
    event.setStatus(EventStatus.PLANNING);
    return transactionalOperator.transactional(
      eventRepository
        .save(event)
        .flatMap(savedEvent ->
                   eventToUserRepository
                     .save(EventToUser.builder()
                             .eventId(savedEvent.getId())
                             .subscribed(true)
                             .userId(event.getCreatorId())
                             .build())
                     .thenReturn(savedEvent))
        .doOnSuccess(savedEvent -> log.info("saved event: {}", savedEvent))
    );
  }

  @Override
  public Mono<Boolean> subscribe(Long userId, Long eventId) {
    log.info("subscribe userid {} to event: {}", userId, eventId);
    return eventRepository.findById(eventId)
      .flatMap(event -> {
        if (!event.getStatus().equals(EventStatus.PLANNING)) {
          return Mono.error(new RuntimeException("Event is not planning"));
        }
        return eventToUserRepository.countByEventIdAndSubscribed(eventId, true)
          .flatMap(count -> {
            if (event.getMaxParticipant() != null && count >= event.getMaxParticipant()) {
              return Mono.error(new RuntimeException("max participant reached")); // TODO: 2024-03-30 EventMaxParticipantReachedException
            }
            return eventToUserRepository.findByEventIdAndUserId(eventId, userId)
              .flatMap(userByEventIdAndUserId -> {
                if (userByEventIdAndUserId.getSubscribed()) {
                  return Mono.error(new RuntimeException("user already subscribed")); // TODO: 2024-03-27 UserAlreadySubscribed
                }
                userByEventIdAndUserId.setSubscribed(true);
                return eventToUserRepository.save(userByEventIdAndUserId)
                  .doOnSuccess(savedEvent -> log.info("userid was {} subscribed event: {}", userId, savedEvent))
                  .thenReturn(true);
              })
              .switchIfEmpty(eventToUserRepository.save(EventToUser.builder()
                                                          .eventId(eventId)
                                                          .userId(userId)
                                                          .subscribed(true)
                                                          .build()).thenReturn(true));
          });
      });
  }

  @Override
  public Mono<Boolean> unsubscribe(Long eventId, Long userId) {
    log.info("unsubscribe userid {} to event: {}", userId, eventId);
    return eventRepository.findById(eventId)
      .flatMap(event -> {
        if (Objects.equals(event.getCreatorId(), userId)) {
          return Mono.error(new RuntimeException("you can only delete this event")); // TODO: 2024-03-26 EventUnSubscribeException
        }
        if (!event.getStatus().equals(EventStatus.PLANNING)) {
          return Mono.error(new RuntimeException("event is done")); // TODO: 2024-03-26 EventUnSubscribeException
        }
        return eventToUserRepository.findByEventIdAndUserId(eventId, userId)
          .flatMap(eventToUser -> {
            eventToUser.setSubscribed(false);
            return eventToUserRepository
              .save(eventToUser)
              .doOnSuccess(user -> log.info("unsubscribe userid {} to event: {}", userId, eventToUser));
          })
          .switchIfEmpty(Mono.error(new RuntimeException("user is not subscribed to this event"))) // TODO: 2024-03-26 EventUnSubscribeException
          .thenReturn(true);
      });
  }

  @Override
  public Flux<Event> getAll(Long creatorId) {
    return eventRepository
      .findAllByCreatorIdNotAndStatus(creatorId, EventStatus.PLANNING)
      .doOnNext(event -> log.info("getAll event: {}", event));
  }

  @Override
  public Flux<Event> getEventsByCreator(Long userId) {
    log.info("get all events by creator {}", userId);
    return eventRepository.findAllByCreatorId(userId);
  }

  @Override
  public Flux<Event> getUsersEventsByUserId(Long userId) {
    log.info("get all events by user {}", userId);
    return eventRepository.findAllUsersEventByUserId(userId)
      .sort(Comparator.comparing(Event::getStartDate))
      .switchIfEmpty(Mono.error(new RuntimeException("user is not subscribed to any event")));
  }

  @Override
  public Mono<Event> getEvent(Long eventId) {
    return eventRepository.findById(eventId);
  }

  @Override
  public Mono<Event> updateStatus(Long eventId, Long userId, EventStatus newEventStatus) {
    log.info("update status for event {} to status {}", eventId, newEventStatus);
    return eventRepository
      .findById(eventId)
      .doOnSuccess(event -> log.info("event was find {}", event))
      .flatMap(event -> {
        if (event.getStatus().equals(EventStatus.COMPLETED)) {
          return Mono.error(new RuntimeException("Event is already completed")); // TODO: 2024-03-26 EventException
        }
        if (!Objects.equals(event.getCreatorId(), userId)) {
          return Mono.error(new RuntimeException("you can not delete this event"));
        }
        event.setStatus(newEventStatus);
        return eventRepository
          .save(event)
          .doOnSuccess(savedEvent -> log.info("status was updated {}", savedEvent));
      })
      .switchIfEmpty(Mono.error(new RuntimeException("event not found"))); // TODO: 2024-03-26 EventNotFoundException
  }

  @Override
  public Flux<Event> getEventsByFilter(EventFilterDto filter, Long userId) {
    log.info("looking for events by filter: {}", filter);
    return eventToUserRepository.findAllByUserIdAndSubscribed(userId)
      .collectList()
      .flatMapMany(ids -> {
        return entityTemplate
          .select(Event.class)
          .matching(getQueryByFilter(filter, ids))
          .all()
          .sort(Comparator.comparing(Event::getStartDate))
          .doOnError(e -> log.error("error finding events", e));
      });
  }

  @Override
  public Flux<EventToUser> getUsersByEvent(Long eventId) {
    return eventToUserRepository.findAllByEventIdAndSubscribed(eventId, Boolean.TRUE);
  }

  private Query getQueryByFilter(EventFilterDto filter, List<Long> ids) {
    System.out.println(ids);
    var query = Query.query(Criteria
                              .from(criteria(filter).and("id").notIn(ids)));
    if (filter.getOffset() != null && filter.getLimit() != null) {
      query = query
        .offset((long) filter.getOffset())
        .limit(filter.getLimit());
    }

    return query;
  }

  private Criteria criteria(EventFilterDto eventFilterDto) {
    var criteria = Criteria.from();

    var timeNow = LocalDateTime.now();
    var filterDate = eventFilterDto.getStartDate() != null &&
                     eventFilterDto
                       .getStartDate()
                       .isAfter(timeNow) ? eventFilterDto.getStartDate() : timeNow;

    if (eventFilterDto.getEventStatus() != null) {
      criteria = criteria.and("status").is(eventFilterDto.getEventStatus());
    }
    if (eventFilterDto.getEndDate() != null) {
      criteria = criteria.and("end_date").lessThan(eventFilterDto.getEndDate());
    }
    criteria = criteria.and("start_date").greaterThan(filterDate);
    if (eventFilterDto.getActivityType() != null) {
      criteria = criteria.and("activity_type").is(eventFilterDto.getActivityType());
    } else if (eventFilterDto.getActivityGroup() != null) {
      criteria = criteria.and("activity_type").in(ActivityType.groupedActivity.get(eventFilterDto.getActivityGroup()));
    }
    return criteria;
  }
}
