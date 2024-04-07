package com.letstogether.chat.service;

import com.letstogether.chat.entity.Message;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageService {
  Mono<Message> addMessage(Message message);

  Flux<Message> getMessagesByChatId(String chatId);
}
