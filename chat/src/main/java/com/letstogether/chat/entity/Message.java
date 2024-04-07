package com.letstogether.chat.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Message")
public class Message {
  @Id
  private String id;
  @Indexed
  private String eventId;
  private String userId;
  private String text;
  private final Long timestamp = System.currentTimeMillis() / 1000L;
}
