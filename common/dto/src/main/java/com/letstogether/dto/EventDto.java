package com.letstogether.dto;

import java.math.BigDecimal;
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
  private String name;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime createdAt;
  private ActivityType activityType;
  private String description;
  private String address;
  private BigDecimal lng;
  private BigDecimal lat;
  private Long creatorId;
  private Integer minParticipant;
  private Integer maxParticipant;
}
