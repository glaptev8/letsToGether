package com.letstogether.authentication.service;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;

public interface ImageService {
  Mono<String> savePhoto(FilePart avatar);
}
