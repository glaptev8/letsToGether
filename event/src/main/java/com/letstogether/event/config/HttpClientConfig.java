package com.letstogether.event.config;

import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;

import com.letstogether.event.client.ChatClient;

@Configuration
public class HttpClientConfig {

  @Bean
  WebClient webClientForChar(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
    return WebClient.builder()
      .baseUrl("http://chat/")
      .filter(lbFunction)
      .build();
  }

  @Bean
  ChatClient postClient(WebClient webClientForChar) {
    HttpServiceProxyFactory httpServiceProxyFactory =
      HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClientForChar))
        .build();
    return httpServiceProxyFactory.createClient(ChatClient.class);
  }
}
