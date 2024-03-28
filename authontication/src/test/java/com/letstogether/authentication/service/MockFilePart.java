package com.letstogether.authentication.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MockFilePart implements FilePart {

  private final String filename;
  private final Flux<DataBuffer> content;

  public MockFilePart(String filename, String content) {
    this.filename = filename;
    DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
    DataBuffer dataBuffer = bufferFactory.wrap(content.getBytes(StandardCharsets.UTF_8));
    this.content = Flux.just(dataBuffer);
  }

  @Override
  public String filename() {
    return filename;
  }

  @Override
  public Flux<DataBuffer> content() {
    return content;
  }

  @Override
  public Mono<Void> transferTo(Path dest) {
    return Mono.empty();
  }

  @Override
  public Mono<Void> transferTo(File dest) {
    return FilePart.super.transferTo(dest);
  }

  @Override
  public Mono<Void> delete() {
    return FilePart.super.delete();
  }

  @Override
  public HttpHeaders headers() {
    return null;
  }

  @Override
  public String name() {
    return "";
  }
}
