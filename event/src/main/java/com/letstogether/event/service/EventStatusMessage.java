package com.letstogether.event.service;

import com.letstogether.dto.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStatusMessage {
  private Long eventId;
  private EventStatus status;
}
