package com.letstogether.authentication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import com.letstogether.authentication.security.AuthenticationManager;
import com.letstogether.authentication.security.BearerTokenServerAuthenticationConverter;
import com.letstogether.authentication.security.JwtHandler;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
@ConditionalOnProperty("jwt.secret")
public class WebSecurityConfig {
  @Value("${jwt.secret}")
  private String secret;
  private String[] publicRoutes = new String[]{"/user/v1/login", "/user/v1/register"};
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authenticationManager) {
    return http
      .csrf().disable()
      .authorizeExchange()
      .pathMatchers(publicRoutes)
      .permitAll()
      .anyExchange()
      .authenticated()
      .and()
      .exceptionHandling()
      .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
      .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
      .and()
      .addFilterAt(bearerAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
      .build();
  }

  private AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authenticationManager) {
    var authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
    authenticationWebFilter.setServerAuthenticationConverter(new BearerTokenServerAuthenticationConverter(new JwtHandler(secret)));
    authenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

    return authenticationWebFilter;
  }
}
