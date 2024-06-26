package com.letstogether.event.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("event_review")
public class EventReview {
  @Id
  private Long id;
  private Long eventId;
  private Long userId;
  private String review;
  private Float grade;
}
