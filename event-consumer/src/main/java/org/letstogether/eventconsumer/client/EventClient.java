package org.letstogether.eventconsumer.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.letstogether.dto.EventDto;
import com.letstogether.dto.UpdateStatusRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@HttpExchange("/event/v1")
public interface EventClient {
  @PostExchange("/updatestatus")
  Mono<EventDto> updateStatus(@RequestBody UpdateStatusRequestDto updateStatusRequest);
}
