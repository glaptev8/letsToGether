package com.letstogether.dto;

public record UserDto(
  String firstName,
  String lastName,
  GenderType gender,
  Integer age,
  String email,
  String phone
) {
}
