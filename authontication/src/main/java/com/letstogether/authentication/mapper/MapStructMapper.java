package com.letstogether.authentication.mapper;

import org.mapstruct.Mapper;

import com.letstogether.authentication.dto.UsersDto;
import com.letstogether.authentication.entity.User;

@Mapper(componentModel = "spring")
public interface MapStructMapper {
  UsersDto fromUser(User user);
  User fromUserDto(UsersDto user);
}
