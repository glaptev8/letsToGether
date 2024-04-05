package com.example.gateway.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gateway.dto.StaticDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/static/v1")
public class StaticController {

  @GetMapping
  @CrossOrigin(origins = "http://localhost:3000")
  public Mono<StaticDto> getStatic() {
    return Mono.just(new StaticDto());
  }
}
