package com.letstogether.authentication.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

public record UsersDto(
  String firstName,
  String lastName,
  GenderType gender,
  Integer are,
  String email,
  String phone
) {
}
