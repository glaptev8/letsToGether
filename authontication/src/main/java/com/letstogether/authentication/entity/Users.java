package com.letstogether.authentication.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.letstogether.authentication.annotation.CreateRepository;
import com.letstogether.authentication.dto.GenderType;

@Table("users")
@CreateRepository
public record Users(
  @Id
  Long id,
  String firstName,
  String lastName,
  GenderType gender,
  Integer are,
  String email,
  String phone,
  @CreatedDate
  LocalDateTime createdAt
  ) {}
