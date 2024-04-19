package org.letstogether.eventconsumer.service;

import org.letstogether.eventconsumer.client.EventClient;
import org.springframework.stereotype.Service;

import com.letstogether.dto.EventStatus;
import com.letstogether.dto.EventStatusMessage;
import com.letstogether.dto.UpdateStatusRequestDto;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EventStatusServiceImpl implements EventStatusService {

  private final EventClient eventClient;

  @Override
  public Mono<Void> updateStatus(EventStatusMessage eventStatusMessage) {
    return eventClient
      .updateStatus(new UpdateStatusRequestDto(eventStatusMessage.getStatus(), eventStatusMessage.getEventId()))
      .then();
  }
}
