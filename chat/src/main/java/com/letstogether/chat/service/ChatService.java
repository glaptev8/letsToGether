package com.letstogether.chat.service;

import com.letstogether.chat.entity.Message;

import com.letstogether.dto.ChatDto;
import reactor.core.publisher.Mono;

public interface ChatService {
  Mono<Boolean> addUserToChat(Long userId, Long chatId);

  Mono<Boolean> removeMemberFromChat(String chatId, String userId);

  Mono<Message> sendMessage(Message message);

  Mono<ChatDto> getChat(Long chatId, Long userId);
}
