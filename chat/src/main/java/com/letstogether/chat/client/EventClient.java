package com.letstogether.chat.client;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.letstogether.dto.EventDto;
import reactor.core.publisher.Flux;

@HttpExchange("/event/v1")
public interface EventClient {
  @PostExchange("/byuser")
  Flux<EventDto> getEvents(@RequestHeader("X-USER-ID") Long userId);
}
