package com.letstogether.authentication.util;

import java.io.FileReader;
import java.io.IOException;

import org.testcontainers.shaded.com.google.common.io.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class JsonReader {

  public static <T> T read(String name, Class<T> clazz) {
    var event = Resources.getResource(name);
    try (var fileReader = new FileReader(event.getPath())) {
      var objectMapper = new ObjectMapper();
      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
      objectMapper.registerModule(new JavaTimeModule());
      return objectMapper.reader().readValue(fileReader, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
