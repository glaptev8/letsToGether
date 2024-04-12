package com.letstogether.event.service;

import com.letstogether.event.entity.Event;
import reactor.core.publisher.Mono;

public interface RabbitSenderService {

  Mono<Void> sendDelayedMessageChangeStatusToInProgress(Event savedEvent);

  Mono<Void> sendDelayedMessageChangeStatusToCompleted(Event savedEvent);
}
