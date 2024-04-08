package com.letstogether.event.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.letstogether.dto.AddUserRequestDto;
import com.letstogether.dto.ChatDto;
import com.letstogether.dto.MessageDto;
import reactor.core.publisher.Mono;

@HttpExchange("/chat/v1")
public interface ChatClient {

  @PostExchange("/adduser")
  Mono<Boolean> addUserToChat(@RequestBody AddUserRequestDto addUserRequestDto);

  @PostExchange("/removeuser")
  Mono<Boolean> removeUser(@RequestBody AddUserRequestDto addUserRequestDto);

  @PostExchange("/send/message")
  Mono<MessageDto> sendMessage(@RequestBody MessageDto message, @RequestHeader("X-USER-ID") Long userId);

  @GetExchange("/{chatId}")
  Mono<ChatDto> getChat(@PathVariable Long chatId, @RequestHeader("X-USER-ID") Long userId);
}
