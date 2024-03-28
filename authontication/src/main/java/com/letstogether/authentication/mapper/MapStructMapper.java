package com.letstogether.authentication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.letstogether.authentication.entity.User;
import com.letstogether.dto.UserDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapStructMapper {

  UserDto fromUser(User user);
  User fromUserDto(UserDto user);
}
