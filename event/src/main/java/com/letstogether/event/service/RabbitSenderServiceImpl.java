package com.letstogether.event.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.letstogether.dto.EventStatusMessage;
import com.letstogether.event.entity.Event;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static com.letstogether.dto.EventStatus.COMPLETED;
import static com.letstogether.dto.EventStatus.IN_PROGRESS;

@Service
@RequiredArgsConstructor
public class RabbitSenderServiceImpl implements RabbitSenderService{

  private final RabbitTemplate rabbitTemplate;
  private final String EXCHANGE_NAME = "delayed_exchange";
  private final String ROUTING_KEY = "event_status_routing_key";
  @Override
  public Mono<Void> sendDelayedMessageChangeStatusToInProgress(Event savedEvent) {
    long delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), savedEvent.getStartDate());
    EventStatusMessage message = new EventStatusMessage(savedEvent.getId(), IN_PROGRESS);

    return sendMessage(message, delay);
  }

  @Override
  public Mono<Void> sendDelayedMessageChangeStatusToCompleted(Event savedEvent) {
    long delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), savedEvent.getStartDate().plusHours(1).plusMinutes(30));
    EventStatusMessage message = new EventStatusMessage(savedEvent.getId(), COMPLETED);

    return sendMessage(message, delay);
  }

  private Mono<Void> sendMessage(EventStatusMessage message, long delay) {
    return Mono.fromCallable(() -> {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message, msg -> {
//          msg.getMessageProperties().setDelayLong(delay);
          return msg;
        });
        return null;
      })
      .subscribeOn(Schedulers.boundedElastic())
      .then();
  }
}
