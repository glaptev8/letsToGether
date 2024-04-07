package com.letstogether.chat.controller;

import com.letstogether.chat.dto.ChatDto;
import com.letstogether.chat.entity.Message;
import com.letstogether.chat.service.ChatService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/v1")
public class MessageController {

  private final ChatService chatService;

  @PostMapping("/send/message")
  public Mono<Message> sendMessage(@RequestBody Message message,
                                   @RequestHeader("X-USER-ID") Long userId) {
    message.setId(userId.toString());
    return chatService.sendMessage(message);
  }

  @PostMapping("/{chatId}")
  public Mono<ChatDto> getChat(@PathVariable Long chatId,
                               @RequestHeader("X-USER-ID") Long userId) {
    return chatService.getChat(chatId, userId);
  }
}
