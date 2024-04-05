package com.letstogether.authentication.config;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class AuthenticationConfig implements WebFluxConfigurer {
  @Value("${upload.path}")
  private String uploadPath;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/auth/v1/img/**")
      .addResourceLocations("file:/" + uploadPath)
      .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic());
  }
}
