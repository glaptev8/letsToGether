package com.letstogether.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventReviewDto {
  private Long id;
  private Long eventId;
  private Long userId;
  private String review;
  private Float grade;
}
