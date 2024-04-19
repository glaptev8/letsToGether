package org.letstogether.eventconsumer.config;

import org.letstogether.eventconsumer.client.EventClient;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class EventConsumerConfig {
  @Bean
  WebClient webClientForChar(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
    return WebClient.builder()
      .baseUrl("http://event/")
      .filter(lbFunction)
      .build();
  }

  @Bean
  EventClient postClient(WebClient webClientForChar) {
    HttpServiceProxyFactory httpServiceProxyFactory =
      HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClientForChar))
        .build();
    return httpServiceProxyFactory.createClient(EventClient.class);
  }
}
