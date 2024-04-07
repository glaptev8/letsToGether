package com.letstogether.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
  private String id;
  private String eventId;
  private String userId;
  private String text;
  private final Long timestamp = System.currentTimeMillis() / 1000L;
}
