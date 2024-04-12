package com.letstogether.event.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

  private final String EXCHANGE_NAME = "delayed_exchange";
  private final String ROUTING_KEY = "event_status_routing_key";
  private final String QUEUE_NAME = "event-status-queue";

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  public CustomExchange delayedExchange() {
    Map<String, Object> args = new HashMap<>();
    args.put("x-delayed-type", "direct");
    return new CustomExchange(EXCHANGE_NAME, "x-delayed-message", true, false, args);
  }

  @Bean
  public Queue queue() {
    return new Queue(QUEUE_NAME);
  }

  @Bean
  public Binding binding(Queue queue, CustomExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY).noargs();
  }
}
