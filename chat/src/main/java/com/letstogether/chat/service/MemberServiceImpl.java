package com.letstogether.chat.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
  private final ReactiveSetOperations<String, String> memberOperations;

  @Override
  public Mono<Boolean> addMemberToChat(Long userId, Long chatId) {
    return memberOperations.add("chat:" + chatId + ":members", userId.toString()).map(count -> count > 0);
  }

  @Override
  public Mono<Boolean> removeMemberFromChat(String chatId, String userId) {
    return memberOperations.remove("chat:" + chatId + ":members", userId).map(count -> count > 0);
  }

  @Override
  public Mono<Set<String>> getChatMembers(String chatId) {
    return memberOperations.members("chat:" + chatId + ":members").collect(Collectors.toSet());
  }
}
