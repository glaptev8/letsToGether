package com.letstogether.chat.service;

import java.util.UUID;

import com.letstogether.chat.entity.Message;

import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final ReactiveListOperations<String, Message> messageOperations;
  private final ReactiveRedisTemplate<String, Message> redisTemplate;
  private final MemberService memberService;
  private final String MESSAGE_KEY_PREFIX = "chat:";

  @Override
  public Mono<Message> addMessage(Message message) {
    return memberService.getChatMembers(message.getEventId())
      .flatMap(users -> {
        if (!users.contains(message.getUserId())) {
          return Mono.error(new RuntimeException("User is not a member of the chat")); // TODO: Заменить на NoPermissionException
        }
        String messageId = UUID.randomUUID().toString();
        message.setId(messageId);
        String key = MESSAGE_KEY_PREFIX + message.getEventId() + ":messages";
        return messageOperations.rightPush(key, message)
          .flatMap(index -> redisTemplate.convertAndSend(MESSAGE_KEY_PREFIX + message.getEventId() + ":messages", message))
          .flatMap(index -> Mono.just(message));
      });
  }

  @Override
  public Flux<Message> getMessagesByChatId(String eventId) {
    String key = MESSAGE_KEY_PREFIX + eventId + ":messages";
    return messageOperations.range(key, 0, -1);
  }
}
