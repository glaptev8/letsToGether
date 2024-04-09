package com.letstogether.chat.websocket;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letstogether.chat.entity.Message;
import com.letstogether.chat.service.MessageService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(MyWebSocketHandler.class);
  private final ReactiveRedisTemplate<String, String> reactiveMemberRedisTemplate;
  private final MessageService messageService;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    String chatId = session.getUri().getPath().split("/")[session.getUri().getPath().split("/").length - 1];
    String redisChannel = "chat:" + chatId + ":messages";
    log.info("setting new connection to chatId {}", chatId);
    reactiveMemberRedisTemplate.listenTo(ChannelTopic.of(redisChannel)).doOnNext(message -> {
      try {
        session.sendMessage(new TextMessage(message.getMessage()));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).subscribe();
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    var objectMapper = new ObjectMapper();
    var messageToChat = objectMapper.readValue(message.getPayload(), Message.class);
    messageToChat.setUserId(session.getHandshakeHeaders().getFirst("x-user-id"));
    messageService
      .addMessage(messageToChat)
      .subscribe();
  }
}
