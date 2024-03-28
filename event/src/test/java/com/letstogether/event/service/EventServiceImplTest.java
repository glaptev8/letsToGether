package com.letstogether.event.service;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.letstogether.dto.ActivityGroup;
import com.letstogether.dto.ActivityType;
import com.letstogether.dto.EventFilterDto;
import com.letstogether.dto.EventStatus;
import com.letstogether.event.config.TestContainerConfig;
import com.letstogether.event.config.TestEnvironmentConfig;
import com.letstogether.event.entity.Event;
import com.letstogether.event.repository.EventToUserRepository;
import com.letstogether.event.util.JsonReader;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ComponentScan(lazyInit = true)
@Import(TestEnvironmentConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class EventServiceImplTest extends TestContainerConfig {

  @Autowired
  private EventServiceImpl eventService;
  @Autowired
  private EventToUserRepository eventToUserRepository;

  @Test
  void save() {
    var event = JsonReader.read("json/event.json", Event.class);

    StepVerifier.create(eventService.save(event))
      .expectNextMatches(savedEvent -> {
        if (savedEvent.getId() == null) {
          return false;
        }
        event.setId(savedEvent.getId());
        return savedEvent.equals(event);
      })
      .verifyComplete();
  }

  @Test
  void subscribe() {
    var event = JsonReader.read("json/event.json", Event.class);
    var subscribingUserId = 2L;

    StepVerifier.create(
        eventService.save(event)
          .flatMap(savedEvent -> eventService
            .subscribe(subscribingUserId, savedEvent.getId())
            .then(eventToUserRepository.findByEventIdAndUserId(savedEvent.getId(), subscribingUserId))))
      .expectNextMatches(eventToUser -> eventToUser != null && eventToUser.getSubscribed())
      .verifyComplete();
  }

  @Test
  void subscribeWhenEventIsCompleted() {
    var event = JsonReader.read("json/event.json", Event.class);
    event.setStatus(EventStatus.COMPLETED);
    var subscribingUserId = 2L;

    StepVerifier.create(
        eventService.save(event)
          .flatMap(savedEvent -> eventService
            .subscribe(subscribingUserId, savedEvent.getId())))
      .expectError(RuntimeException.class)
      .verify();
  }

  @Test
  void subscribeWhenAlreadySubscribed() {
    var event = JsonReader.read("json/event.json", Event.class);
    var subscribingUserId = 1L;

    StepVerifier.create(
        eventService.save(event)
          .flatMap(savedEvent -> eventService
            .subscribe(subscribingUserId, savedEvent.getId())))
      .expectError(RuntimeException.class)
      .verify();
  }

  @Test
  void unsubscribe() {
    var event = JsonReader.read("json/event.json", Event.class);
    var unSubscribingUserId = 2L;

    StepVerifier.create(
        eventService.save(event)
          .flatMap(savedEvent -> eventService
            .subscribe(unSubscribingUserId, event.getId())
            .then(eventService.unsubscribe(savedEvent.getId(), unSubscribingUserId))
            .then(eventToUserRepository.findByEventIdAndUserId(savedEvent.getId(), unSubscribingUserId))))
      .expectNextMatches(eventToUser -> eventToUser != null && !eventToUser.getSubscribed())
      .verifyComplete();
  }

  @Test
  void unsubscribeWhenCreator() {
    var event = JsonReader.read("json/event.json", Event.class);
    var unSubscribingUserId = 1L;

    StepVerifier.create(
        eventService.save(event)
          .flatMap(savedEvent -> eventService
            .subscribe(unSubscribingUserId, event.getId())
            .then(eventService.unsubscribe(savedEvent.getId(), unSubscribingUserId))))
      .expectError(RuntimeException.class)
      .verify();
  }

  @Test
  void unsubscribeWhenTestIsNotPlanning() {
    var event = JsonReader.read("json/event.json", Event.class);
    event.setStatus(EventStatus.COMPLETED);
    var unSubscribingUserId = 2L;

    StepVerifier.create(
        eventService.save(event)
          .flatMap(savedEvent -> eventService
            .subscribe(unSubscribingUserId, event.getId())
            .then(eventService.unsubscribe(savedEvent.getId(), unSubscribingUserId))))
      .expectError(RuntimeException.class)
      .verify();
  }

  @Test
  void getUsersEventsByUserId() {
    List<Event> events = List.of(
      JsonReader.read("json/event.json", Event.class),
      JsonReader.read("json/event.json", Event.class),
      JsonReader.read("json/event.json", Event.class));
    var userId = 1000L;

    StepVerifier.create(
        Flux.fromIterable(events)
          .flatMap(event -> eventService.save(event))
          .flatMap(savedEvent -> eventService.subscribe(userId, savedEvent.getId()))
          .thenMany(eventService.getUsersEventsByUserId(userId))
          .collectList()
      )
      .consumeNextWith(eventsList -> {
        assertEquals(3, eventsList.size());
      })
      .verifyComplete();
  }

  @Test
  void updateStatus() {
    var event = JsonReader.read("json/event.json", Event.class);

    StepVerifier.create(
        eventService.save(event)
          .flatMap(savedEvent -> eventService.updateStatus(event.getId(), EventStatus.COMPLETED)
            .then(eventService.getEvent(event.getId()))))
      .expectNextMatches(updatedEvent -> updatedEvent.getStatus().equals(EventStatus.COMPLETED))
      .verifyComplete();
  }

  @Test
  void updateAlreadyCompletedStatus() {
    var event = JsonReader.read("json/event.json", Event.class);
    event.setStatus(EventStatus.COMPLETED);

    StepVerifier.create(
        eventService.save(event)
          .flatMap(savedEvent -> eventService.updateStatus(event.getId(), EventStatus.COMPLETED)))
      .expectError(RuntimeException.class)
      .verify();
  }

  @Test
  void getEventsByOnlyStatusFilter() {
    var event = JsonReader.read("json/event.json", Event.class);
    var event2 = JsonReader.read("json/event.json", Event.class);
    var event3 = JsonReader.read("json/event.json", Event.class);
    var event4 = JsonReader.read("json/event.json", Event.class);
    event.setStatus(EventStatus.COMPLETED);
    event2.setStatus(EventStatus.COMPLETED);
    event3.setStatus(EventStatus.COMPLETED);
    var eventFilter = EventFilterDto.builder()
      .eventStatus(EventStatus.COMPLETED)
      .build();

    StepVerifier.create(
        Flux.fromIterable(List.of(event, event2, event3, event4))
          .flatMap(e -> eventService.save(e))
          .thenMany(eventService.getEventsByFilter(eventFilter))
          .collectList()
      )
      .consumeNextWith(eventsByFilter -> {
        assertEquals(3, eventsByFilter.size());
        assertTrue(eventsByFilter.stream().allMatch(e -> e.getStatus().equals(EventStatus.COMPLETED)));
      })
      .verifyComplete();
  }

  @Test
  void getEventsByOnlyStartDateFilter() {
    var event = JsonReader.read("json/event.json", Event.class);
    var event2 = JsonReader.read("json/event.json", Event.class);
    var event3 = JsonReader.read("json/event.json", Event.class);
    var event4 = JsonReader.read("json/event.json", Event.class);
    event.setStartDate(LocalDateTime.of(2324, 4, 23, 16, 30));
    event.setEndDate(event.getStartDate().plusDays(3));
    event2.setStartDate(LocalDateTime.of(2324, 4, 24, 16, 30));
    event2.setEndDate(event2.getStartDate().plusDays(3));
    event3.setStartDate(LocalDateTime.of(2324, 5, 23, 16, 30));
    event3.setEndDate(event3.getStartDate().plusDays(3));
    event4.setStartDate(LocalDateTime.of(2324, 6, 23, 16, 30));
    event4.setEndDate(event4.getStartDate().plusDays(3));
    var filterDate = LocalDateTime.of(2324, 2, 15, 16, 30);
    var filterDate2 = LocalDateTime.of(2324, 5, 15, 16, 30);
    var eventFilter = EventFilterDto.builder()
      .startDate(filterDate)
      .build();
    var eventFilter2 = EventFilterDto.builder()
      .startDate(filterDate2)
      .build();

    StepVerifier.create(
        Flux.fromIterable(List.of(event, event2, event3, event4))
          .flatMap(e -> eventService.save(e))
          .thenMany(eventService.getEventsByFilter(eventFilter))
          .collectList()
      )
      .consumeNextWith(eventsByFilter -> {
        assertEquals(4, eventsByFilter.size());
        assertTrue(eventsByFilter.stream().allMatch(e -> e.getStartDate().isAfter(filterDate)));
      })
      .verifyComplete();

    StepVerifier.create(
        Flux.fromIterable(List.of(event, event2, event3, event4))
          .thenMany(eventService.getEventsByFilter(eventFilter2))
          .collectList()
      )
      .consumeNextWith(eventsByFilter -> {
        assertEquals(2, eventsByFilter.size());
        assertTrue(eventsByFilter.stream().allMatch(e -> e.getStartDate().isAfter(filterDate2)));
      })
      .verifyComplete();
  }

  @Test
  void getEventsByOnlyStartDateAndEndDateFilter() {
    var event = JsonReader.read("json/event.json", Event.class);
    var event2 = JsonReader.read("json/event.json", Event.class);
    var event3 = JsonReader.read("json/event.json", Event.class);
    var event4 = JsonReader.read("json/event.json", Event.class);
    event.setStartDate(LocalDateTime.now().plusMonths(2).plusDays(5));
    event.setEndDate(event.getStartDate().plusDays(10));
    event2.setStartDate(LocalDateTime.now().plusMonths(2).plusDays(8));
    event2.setEndDate(event2.getStartDate().plusDays(10));
    event3.setStartDate(LocalDateTime.now().plusMonths(5).plusDays(5));
    event3.setEndDate(event3.getStartDate().plusDays(10));
    event4.setStartDate(LocalDateTime.now().plusMonths(6).plusDays(5));
    event4.setEndDate(event4.getStartDate().plusDays(10));
    var filterStartDate = LocalDateTime.now().plusDays(1);
    var filterEndDate = LocalDateTime.now().plusMonths(4);
    var eventFilter = EventFilterDto.builder()
      .startDate(filterStartDate)
      .endDate(filterEndDate)
      .build();

    StepVerifier.create(
        Flux.fromIterable(List.of(event, event2, event3, event4))
          .flatMap(e -> eventService.save(e))
          .thenMany(eventService.getEventsByFilter(eventFilter))
          .collectList()
      )
      .consumeNextWith(eventsByFilter -> {
        assertEquals(2, eventsByFilter.size());
        assertTrue(eventsByFilter.stream().allMatch(e -> e.getStartDate().isAfter(filterStartDate)));
        assertTrue(eventsByFilter.stream().allMatch(e -> e.getEndDate().isBefore(filterEndDate)));
      })
      .verifyComplete();
  }

  @Test
  void getEventsByOnlyActivityTypeFilter() {
    var event = JsonReader.read("json/event.json", Event.class);
    var event2 = JsonReader.read("json/event.json", Event.class);
    var event3 = JsonReader.read("json/event.json", Event.class);
    var event4 = JsonReader.read("json/event.json", Event.class);
    event.setActivityType(ActivityType.BASKETBALL);
    event.setActivityType(ActivityType.BASKETBALL);
    event2.setActivityType(ActivityType.FOOTBALL);
    event3.setActivityType(ActivityType.CINEMA);
    event4.setActivityType(ActivityType.BASKETBALL);
    var eventFilter = EventFilterDto.builder()
      .activityType(ActivityType.BASKETBALL)
      .build();

    StepVerifier.create(
        Flux.fromIterable(List.of(event, event2, event3, event4))
          .flatMap(e -> eventService.save(e))
          .thenMany(eventService.getEventsByFilter(eventFilter))
          .collectList()
      )
      .consumeNextWith(eventsByFilter -> {
        assertEquals(2, eventsByFilter.size());
        assertTrue(eventsByFilter.stream().allMatch(e -> e.getActivityType().equals(ActivityType.BASKETBALL)));
      })
      .verifyComplete();
  }

  @Test
  void getEventsByOnlyActivityGroupFilter() {
    var event = JsonReader.read("json/event.json", Event.class);
    var event2 = JsonReader.read("json/event.json", Event.class);
    var event3 = JsonReader.read("json/event.json", Event.class);
    var event4 = JsonReader.read("json/event.json", Event.class);
    event.setActivityType(ActivityType.BASKETBALL);
    event2.setActivityType(ActivityType.FOOTBALL);
    event3.setActivityType(ActivityType.CINEMA);
    event4.setActivityType(ActivityType.BASKETBALL);
    var activityGroupFilter = ActivityGroup.SPORT;
    var eventFilter = EventFilterDto.builder()
      .activityGroup(activityGroupFilter)
      .build();

    StepVerifier.create(
        Flux.fromIterable(List.of(event, event2, event3, event4))
          .flatMap(e -> eventService.save(e))
          .thenMany(eventService.getEventsByFilter(eventFilter))
          .collectList()
      )
      .consumeNextWith(eventsByFilter -> {
        assertEquals(3, eventsByFilter.size());
        assertTrue(eventsByFilter.stream().allMatch(e -> ActivityType.groupedActivity.get(activityGroupFilter).contains(e.getActivityType())));
      })
      .verifyComplete();
  }
}