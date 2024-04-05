package com.letstogether.event.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.letstogether.dto.ActivityType;
import com.letstogether.dto.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Table("event")
@NoArgsConstructor
@AllArgsConstructor
public class Event {
  @Id
  private Long id;
  private EventStatus status;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  @CreatedDate
  private LocalDateTime createdAt;
  private ActivityType activityType;
  private String description;
  private String address;
  private String name;
  private BigDecimal lng;
  private BigDecimal lat;
  private Long creatorId;
  private Integer minParticipant;
  private Integer maxParticipant;
}
