package com.letstogether.authentication.mapper;

import org.mapstruct.Mapper;

import com.letstogether.authentication.dto.UsersDto;
import com.letstogether.authentication.entity.Users;

@Mapper(componentModel = "spring")
public interface MapStructMapper {
  UsersDto fromUser(Users user);
  Users fromUserDto(UsersDto user);
}
