package com.letstogether.authentication.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.letstogether.dto.GenderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"pathToAvatar", "password"})
public class User {
  @Id
  private Long id;
  private String firstName;
  private String password;
  private String lastName;
  private GenderType gender;
  private Integer age;
  private String pathToAvatar;
  private String email;
  private String phone;
  @CreatedDate
  private LocalDateTime createdAt;
}
