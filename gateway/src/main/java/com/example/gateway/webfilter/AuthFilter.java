package com.example.gateway.webfilter;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {
  private final WebClient webClient;

  private final String[] publicRoutes = new String[]{"/auth/v1/login", "/auth/v1/register"};

  @Autowired
  public AuthFilter(WebClient.Builder webClientBuilder, ReactorLoadBalancerExchangeFilterFunction lbFunction) {
    this.webClient = webClientBuilder.baseUrl("http://authentication").filter(lbFunction).build();
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();

    if (Arrays.asList(publicRoutes).contains(path)) {
      return chain.filter(exchange);
    }

    String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    return webClient.post()
      .uri("/auth/v1")
      .header("Authorization", token)
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new RuntimeException("Unauthorized")))
      .bodyToMono(Long.class)
      .flatMap(userId -> {
        exchange.getRequest().mutate().header("userId", userId.toString()).build();
        return chain.filter(exchange);
      })
      .onErrorResume(e -> {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      });
  }

  @Override
  public int getOrder() {
    return -100;
  }
}
