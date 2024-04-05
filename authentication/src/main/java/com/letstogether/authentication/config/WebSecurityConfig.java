package com.letstogether.authentication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

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
  private final String[] publicRoutes = new String[]{
    "/auth/v1/login",
    "/auth/v1/register",
    "/auth/v1/img/**",
    "/auth/v1/test",
    "/auth/v2/api-docs",
    "/auth/swagger-resources",
    "/auth/swagger-resources/**",
    "/auth/configuration/ui",
    "/auth/configuration/security",
    "/auth/swagger-ui.html",
    "/auth/webjars/**",
    "/auth/v3/api-docs/**",
    "/auth/swagger-ui/**",
    "/v2/api-docs",
    "/swagger-resources",
    "/swagger-resources/**",
    "/configuration/ui",
    "/configuration/security",
    "/swagger-ui.html",
    "/webjars/**",
    "/v3/api-docs/**",
    "/swagger-ui/**"};

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                       AuthenticationManager authenticationManager) {
    return http
      .csrf().disable()
      .authorizeExchange()
      .pathMatchers(HttpMethod.OPTIONS).permitAll()
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
