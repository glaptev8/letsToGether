package com.letstogether.chat.config;

import com.letstogether.chat.entity.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class ChatConfig {
  @Bean
  public RedisSerializer<Message> messageSerializer() {
    return new Jackson2JsonRedisSerializer<>(Message.class);
  }

  @Bean
  public ReactiveRedisTemplate<String, Message> reactiveMessageRedisTemplate(
    ReactiveRedisConnectionFactory factory,
    RedisSerializer<Message> messageSerializer) {

    StringRedisSerializer keySerializer = new StringRedisSerializer();

    RedisSerializationContext<String, Message> serializationContext = RedisSerializationContext
      .<String, Message>newSerializationContext(keySerializer)
      .value(messageSerializer)
      .build();

    return new ReactiveRedisTemplate<>(factory, serializationContext);
  }

  @Bean
  public ReactiveRedisTemplate<String, String> reactiveMemberRedisTemplate(
    ReactiveRedisConnectionFactory factory) {
    return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
  }

  @Bean
  public ReactiveListOperations<String, Message> messageOperations(ReactiveRedisTemplate<String, Message> reactiveMessageRedisTemplate) {
    return reactiveMessageRedisTemplate.opsForList();
  }

  @Bean
  public ReactiveSetOperations<String, String> memberOperations(ReactiveRedisTemplate<String, String> reactiveMemberRedisTemplate) {
    return reactiveMemberRedisTemplate.opsForSet();
  }

  @Bean
  RedisMessageListenerContainer redisMessageListenerContainer(LettuceConnectionFactory connectionFactory) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    return container;
  }
}
