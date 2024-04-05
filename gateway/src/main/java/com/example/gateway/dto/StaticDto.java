package com.example.gateway.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.letstogether.dto.ActivityGroup;
import com.letstogether.dto.ActivityType;
import com.letstogether.dto.EventStatus;
import com.letstogether.dto.GenderType;
import com.letstogether.dto.HobbyType;
import jakarta.inject.Singleton;
import lombok.Data;

@Data
@Singleton
public class StaticDto {
  private Map<ActivityGroup, List<ActivityType>> activityGroups = ActivityType.groupedActivity;
  private Set<GenderType> genderTypes = Set.of(GenderType.values());
  private Set<EventStatus> eventStatuses = Set.of(EventStatus.values());
  private Set<HobbyType> hobbyTypes = HobbyType.hobbies;
}
