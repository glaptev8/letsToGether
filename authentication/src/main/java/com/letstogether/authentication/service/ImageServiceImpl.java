package com.letstogether.authentication.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
  private final ResourceLoader resourceLoader;
  @Value("${upload.path}")
  private String uploadPath;

  public Mono<String> savePhoto(FilePart avatar) {
    log.info("saving photo {}", avatar);
    if (avatar == null) {
      return Mono.empty();
    }

    var newAvatarName = UUID.randomUUID() + "_" + avatar.filename();
    var pathToFile = Paths.get(uploadPath, newAvatarName);
    File uploadDir = pathToFile.getParent().toFile();
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }

    return avatar
      .transferTo(pathToFile.toFile())
      .then(Mono.just(newAvatarName))
      .doOnError(e -> log.error("saving file error", e));
  }
}
