package com.letstogether.chat.service;

import java.util.Set;

import reactor.core.publisher.Mono;

public interface MemberService {
  Mono<Boolean> addMemberToChat(Long userId, Long chatId);

  Mono<Boolean> removeMemberFromChat(String chatId, String userId);

  Mono<Set<String>> getChatMembers(String chatId);
}
