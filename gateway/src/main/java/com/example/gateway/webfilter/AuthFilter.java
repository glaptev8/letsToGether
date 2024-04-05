package com.example.gateway.webfilter;

import java.util.Arrays;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;

import com.letstogether.exception.ApiException;
import com.letstogether.exception.AuthException;
import com.letstogether.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

  private final WebClient webClient;

  private final String[] publicRoutes = new String[]{
    "/auth/v1/login",
    "/auth/v1/register",
    "/auth/v1/img/**",
    "/static/v1"};

  @Autowired
  public AuthFilter(WebClient.Builder webClientBuilder, ReactorLoadBalancerExchangeFilterFunction lbFunction) {
    this.webClient = webClientBuilder.baseUrl("http://authentication").filter(lbFunction).build();
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    PathMatcher pathMatcher = new AntPathMatcher();
    String path = exchange.getRequest().getPath().value();

    boolean isPublicRoute = Arrays.stream(publicRoutes)
      .anyMatch(route -> pathMatcher.match(route, path));

    if (isPublicRoute) {
      return chain.filter(exchange);
    }

    String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    return webClient.post()
      .uri("/auth/v1")
      .header("Authorization", token)
      .retrieve()
      .bodyToMono(Long.class)
      .flatMap(userId -> {
        exchange.getRequest().mutate().header("X-USER-ID", userId.toString()).build();
        return chain.filter(exchange);
      })
      .onErrorResume(e -> {
        if (e instanceof UnauthorizedException || e instanceof AuthException) {
          exchange.getResponse().setStatusCode(UNAUTHORIZED);
          return exchange.getResponse().setComplete(); // Завершаем обработку запроса с ошибкой авторизации
        } else if (e instanceof WebClientResponseException webClientResponseException) {
          exchange.getResponse().setStatusCode(webClientResponseException.getStatusCode());
          return exchange.getResponse().setComplete();
        } else {
          exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
          return exchange.getResponse().setComplete();
        }
      });
  }

  @Override
  public int getOrder() {
    return -100;
  }
}
