package com.letstogether.authentication.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import com.letstogether.dto.GenderType;
import com.letstogether.dto.HobbyType;
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
  private String aboutMe;
  @Transient
  private Set<HobbyType> hobbies;
  private GenderType gender;
  private Integer age;
  private String pathToAvatar;
  private String email;
  private String phone;
  @CreatedDate
  private LocalDateTime createdAt;
}
