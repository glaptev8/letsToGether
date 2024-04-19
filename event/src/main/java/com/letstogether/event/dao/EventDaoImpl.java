package com.letstogether.event.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.flywaydb.core.internal.util.CollectionsUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.letstogether.dto.ActivityType;
import com.letstogether.dto.EventFilterDto;
import com.letstogether.dto.EventStatus;
import com.letstogether.event.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import static com.letstogether.dto.EventStatus.COMPLETED;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventDaoImpl implements EventDao {

  private final R2dbcEntityTemplate template;

  @Override
  public Flux<Event> findEventsByFilter(EventFilterDto filter, List<Long> ids) {
    String sql = buildSqlQuery(filter, ids);
    log.info(sql);
    return template.getDatabaseClient()
      .sql(sql)
      .map((row, metadata) -> {
        Event event = new Event();
        event.setId(row.get("id", Long.class));
        event.setName(row.get("name", String.class));
        event.setStatus(EventStatus.valueOf(row.get("status", String.class)));
        event.setStartDate(row.get("start_date", LocalDateTime.class));
        event.setEndDate(row.get("end_date", LocalDateTime.class));
        event.setCreatedAt(row.get("created_at", LocalDateTime.class));
        event.setDescription(row.get("description", String.class));
        event.setAddress(row.get("address", String.class));
        event.setLng(row.get("lng", BigDecimal.class));
        event.setLat(row.get("lat", BigDecimal.class));
        event.setCreatorId(row.get("creator_id", Long.class));
        event.setMinParticipant(row.get("min_participant", Integer.class));
        event.setMaxParticipant(row.get("max_participant", Integer.class));
        event.setActivityType(ActivityType.valueOf(row.get("activity_type", String.class)));
        return event;
      })
      .all();
  }

  private String buildSqlQuery(EventFilterDto eventFilterDto, List<Long> ids) {
    StringBuilder sql = new StringBuilder("SELECT * FROM event WHERE ");

    var timeNow = LocalDateTime.now();
    var filterDate = eventFilterDto.getStartDate() != null && eventFilterDto.getStartDate().isAfter(timeNow)
                     ? eventFilterDto.getStartDate() : timeNow;

    List<String> conditions = new ArrayList<>();

    if (eventFilterDto.getEventStatus() != null) {
      conditions.add("status = '" + eventFilterDto.getEventStatus() + "'");
    } else {
      conditions.add("status = 'PLANNING'");
    }

    if (eventFilterDto.getEndDate() != null) {
      conditions.add("end_date < '" + eventFilterDto.getEndDate().toString() + "'");
    }

    if (eventFilterDto.getEventStatus() != COMPLETED) {
      conditions.add("start_date > '" + filterDate.toString() + "'");
    } else {
      conditions.add("start_date < NOW()");
    }

    if (eventFilterDto.getActivityType() != null) {
      conditions.add("activity_type = '" + eventFilterDto.getActivityType() + "'");
    } else if (eventFilterDto.getActivityGroup() != null) {
      List<String> activityTypes = ActivityType.groupedActivity.get(eventFilterDto.getActivityGroup()).stream()
        .map(Enum::name)
        .collect(Collectors.toList());
      conditions.add("activity_type IN ('" + String.join("', '", activityTypes) + "')");
    }

    if (!CollectionUtils.isEmpty(ids)) {
      if (eventFilterDto.getOwn()) {
        conditions.add("id IN (" + ids.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")");
      } else {
        conditions.add("id NOT IN (" + ids.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")");
      }

      if (eventFilterDto.getLat() != null && eventFilterDto.getLng() != null && eventFilterDto.getRadius() != null) {
        conditions.add("ST_DWithin(ST_SetSRID(ST_MakePoint(lng, lat), 4326)::geography, " +
                       "ST_SetSRID(ST_MakePoint(" + eventFilterDto.getLng() + ", " + eventFilterDto.getLat() + "), 4326)::geography, " +
                       eventFilterDto.getRadius() * 1000 + ")");
      }
    }


    sql.append(String.join(" AND ", conditions));
    return sql.toString();
  }

}
