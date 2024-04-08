package com.letstogether.chat.controller;

import com.letstogether.chat.entity.Message;
import com.letstogether.chat.mapper.MapStructMapper;
import com.letstogether.chat.service.ChatService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.letstogether.dto.AddUserRequestDto;
import com.letstogether.dto.ChatDto;
import com.letstogether.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/v1")
public class MessageController {

  private final ChatService chatService;
  private final MapStructMapper mapper;

  @PostMapping("/adduser")
  public Mono<Boolean> addUserToChat(@RequestBody AddUserRequestDto addUserRequestDto) {
    log.info("addUserToChat addUserRequestDto: {}", addUserRequestDto);
    return chatService.addUserToChat(addUserRequestDto.getUserId(), addUserRequestDto.getChatId());
  }

  @PostMapping("/removeuser")
  public Mono<Boolean> removeUser(@RequestBody AddUserRequestDto addUserRequestDto) {
    log.info("removeUser addUserRequestDto: {}", addUserRequestDto);
    return chatService.removeMemberFromChat(addUserRequestDto.getChatId().toString(), addUserRequestDto.getUserId().toString());
  }

  @PostMapping("/send/message")
  public Mono<MessageDto> sendMessage(@RequestBody Message message,
                                      @RequestHeader("X-USER-ID") Long userId) {
    log.info("sendMessage message: {}", message);
    message.setId(userId.toString());
    return chatService.sendMessage(message).map(mapper::toDto);
  }

  @PostMapping("/{chatId}")
  public Mono<ChatDto> getChat(@PathVariable Long chatId,
                               @RequestHeader("X-USER-ID") Long userId) {
    log.info("getChat chatId: {}", chatId);
    return chatService.getChat(chatId, userId);
  }
}
