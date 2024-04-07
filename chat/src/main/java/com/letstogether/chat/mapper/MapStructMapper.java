package com.letstogether.chat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.letstogether.chat.entity.Message;
import com.letstogether.dto.MessageDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapStructMapper {

  MessageDto toDto(Message message);
}
