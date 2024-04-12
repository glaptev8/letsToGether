package com.letstogether.event.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
  @Bean
  public CustomExchange delayedExchange() {
    Map<String, Object> args = new HashMap<>();
    args.put("x-delayed-type", "direct");
    return new CustomExchange("my-delayed-exchange", "x-delayed-message", true, false, args);
  }

  @Bean
  public Queue queue() {
    return new Queue("my-queue");
  }

  @Bean
  public Binding binding(Queue queue, CustomExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with("my-routing-key").noargs();
  }
}
