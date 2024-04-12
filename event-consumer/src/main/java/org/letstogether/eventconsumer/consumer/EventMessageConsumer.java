package org.letstogether.eventconsumer.consumer;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.letstogether.dto.EventStatusMessage;

public class EventMessageConsumer {
  private final String EXCHANGE_NAME = "delayed_exchange";
  private final String ROUTING_KEY = "event_status_routing_key";
  private final String QUEUE_NAME = "event-status-queue";

  @RabbitListener(bindings = @QueueBinding(
    value = @Queue(value = QUEUE_NAME, durable = "true"),
    exchange = @Exchange(value = EXCHANGE_NAME),
    key = ROUTING_KEY
  ))
  public void receiveMessage(EventStatusMessage message) {
    System.out.println("Received message: " + message);
  }
}
