package com.letstogether.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class UserDto {
  private Long id;
  private String firstName;
  private String lastName;
  private GenderType gender;
  private Integer age;
  private Set<HobbyType> hobbies;
  private String aboutMe;
  private String pathToAvatar;
  private String email;
  private String phone;
}
