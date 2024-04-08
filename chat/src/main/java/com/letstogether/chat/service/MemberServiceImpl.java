package com.letstogether.chat.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
  private final ReactiveSetOperations<String, String> memberOperations;

  @Override
  public Mono<Boolean> addMemberToChat(Long userId, Long chatId) {
    return memberOperations.add("chat:" + chatId + ":members", userId.toString()).flatMap(count -> {
      if (count <= 0) {
        return Mono.error(new RuntimeException(String.format("user %s was not added from chat %s", userId, chatId)));
      }
      return Mono.just(true);
    });
  }

  @Override
  public Mono<Boolean> removeMemberFromChat(String chatId, String userId) {
    log.info("Removing member from member {} chat {}", userId, chatId);
    return memberOperations.remove("chat:" + chatId + ":members", userId).flatMap(count -> {
      if (count <= 0) {
        return Mono.error(new RuntimeException(String.format("user %s was not deleted from chat %s", userId, chatId)));
      }
     return Mono.just(true);
    });
  }

  @Override
  public Mono<Set<String>> getChatMembers(String chatId) {
    return memberOperations.members("chat:" + chatId + ":members").collect(Collectors.toSet());
  }
}
