package com.letstogether.event.dao;

import java.util.List;

import com.letstogether.dto.EventFilterDto;
import com.letstogether.event.entity.Event;
import reactor.core.publisher.Flux;

public interface EventDao {
  Flux<Event> findEventsByFilter(EventFilterDto filter, List<Long> ids);
}
