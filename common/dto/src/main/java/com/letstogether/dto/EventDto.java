package com.letstogether.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
  private Long id;
  private EventStatus status;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime createdAt;
  private ActivityType activityType;
  private String description;
  private String address;
  private Long lng;
  private Long lat;
  private Long creatorId;
  private Integer minParticipant;
  private Integer maxParticipant;
}
