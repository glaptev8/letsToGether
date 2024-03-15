package com.letstogether.messagesourcestarter.service;

public interface MessageSourceService {
  String logMessage(String sourceKey, Object ... objects);
}
