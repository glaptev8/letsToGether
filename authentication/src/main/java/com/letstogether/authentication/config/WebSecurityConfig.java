package com.letstogether.authentication.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letstogether.authentication.entity.User;
import com.letstogether.authentication.security.AuthenticationManager;
import com.letstogether.authentication.security.BearerTokenServerAuthenticationConverter;
import com.letstogether.authentication.security.JwtHandler;
import com.letstogether.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
@ConditionalOnProperty("jwt.secret")
public class WebSecurityConfig {

  @Value("${jwt.secret}")
  private String secret;
  @Value("letstogether.ui.uri")
  private String redirectUri;
  private final UserService userService;
  private final String[] publicRoutes = new String[]{
    "/auth/v1/login/google",
    "/oauth2/**",
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
    http
      .csrf().disable()
      .authorizeExchange()
      .pathMatchers(HttpMethod.OPTIONS, "/oauth2/**").permitAll()  // OAuth2 endpoints
      .pathMatchers(publicRoutes).permitAll()
      .pathMatchers("/auth/v1/user/**").authenticated()  // API endpoints requiring JWT
      .and()
      .addFilterAt(bearerAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)  // JWT filter
      .oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec.authenticationSuccessHandler((webFilterExchange, authentication) -> {
          OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
          return userService.login(User.builder()
                                     .firstName(oauthUser.getAttribute("given_name"))
                                     .lastName(oauthUser.getAttribute("family_name"))
                                     .email(oauthUser.getAttribute("email"))
                                     .providerId(oauthUser.getAttribute("sub"))
                                     .build())
            .flatMap(token -> {
              String redirectUrl = redirectUri + "/auth-success?token=" + token.token() + "&userId=" + token.userId() + "&isFirstEnter=" + token.isFirstEnter();
              ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
              response.setStatusCode(HttpStatus.SEE_OTHER);
              response.getHeaders().setLocation(URI.create(redirectUrl));
              return response.setComplete();
            });
        })
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()))  // OAuth2 configuration
      .authorizeExchange()
      .anyExchange().authenticated()
      .and()
      .exceptionHandling()
      .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
      .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)));

    return http.build();
  }

  private AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authenticationManager) {
    var authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
    authenticationWebFilter.setServerAuthenticationConverter(new BearerTokenServerAuthenticationConverter(new JwtHandler(secret)));
    authenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

    return authenticationWebFilter;
  }
}
