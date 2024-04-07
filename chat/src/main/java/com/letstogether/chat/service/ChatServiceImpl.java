package com.letstogether.chat.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.letstogether.chat.entity.Message;

import org.springframework.stereotype.Service;

import com.letstogether.chat.mapper.MapStructMapper;
import com.letstogether.dto.ChatDto;
import com.letstogether.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final MemberService memberService;
  private final MessageService messageService;
  private final MapStructMapper mapper;

  @Override
  public Mono<Boolean> addUserToChat(Long userId, Long chatId) {
    return memberService.addMemberToChat(userId, chatId);
  }

  @Override
  public Mono<Boolean> removeMemberFromChat(String chatId, String userId) {
    return memberService.removeMemberFromChat(chatId, userId);
  }

  @Override
  public Mono<Message> sendMessage(Message message) {
    return memberService.getChatMembers(message.getEventId())
      .flatMap(members -> {
        if (members.contains(message.getUserId())) {
          return messageService.addMessage(message);
        } else {
          return Mono.error(new RuntimeException("User is not a member of the chat")); // TODO: 2024-04-06 NoPermissionException
        }
      });
  }

  @Override
  public Mono<ChatDto> getChat(Long chatId, Long userId) {
    return memberService.getChatMembers(chatId.toString())
      .flatMap(members -> {
        if (!members.contains(userId.toString())) {
          return Mono.error(new RuntimeException("User is not a member of the chat")); // TODO: Заменить на NoPermissionException
        }
        Mono<Set<Long>> membersMono = Mono.just(members.stream().map(Long::valueOf).collect(Collectors.toSet()));
        Mono<List<MessageDto>> messagesMono = messageService
          .getMessagesByChatId(chatId.toString())
          .map(mapper::toDto)
          .collectList();

        return Mono.zip(membersMono, messagesMono, (m, msgs) -> {
          var chatDto = new ChatDto();
          chatDto.setUsersId(m);
          chatDto.setMessages(msgs);
          return chatDto;
        });
      });
  }
}
