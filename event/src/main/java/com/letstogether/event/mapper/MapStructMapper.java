package com.letstogether.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.letstogether.dto.EventDto;
import com.letstogether.dto.EventReviewDto;
import com.letstogether.event.entity.Event;
import com.letstogether.event.entity.EventReview;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapStructMapper {
  EventDto toDto(Event event);
  Event toEvent(EventDto eventDto);
  EventReviewDto eventReviewDto(EventReview eventReview);
}
