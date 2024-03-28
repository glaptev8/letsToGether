package com.letstogether.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDto {
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private EventStatus eventStatus;
  private ActivityType activityType;
  private ActivityGroup activityGroup;
  private Integer offset = 0;
  private Integer limit = 100;
}
