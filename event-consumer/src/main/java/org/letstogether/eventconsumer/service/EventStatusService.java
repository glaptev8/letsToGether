package org.letstogether.eventconsumer.service;

import com.letstogether.dto.EventStatus;
import com.letstogether.dto.EventStatusMessage;
import reactor.core.publisher.Mono;

public interface EventStatusService {
  Mono<Void> updateStatus(EventStatusMessage eventStatus);
}
