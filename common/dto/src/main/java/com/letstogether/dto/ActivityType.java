package com.letstogether.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import static com.letstogether.dto.ActivityGroup.EDUCATION;
import static com.letstogether.dto.ActivityGroup.ENTERTAINMENT;
import static com.letstogether.dto.ActivityGroup.SPORT;

@Getter
public enum ActivityType {
  FOOTBALL(SPORT),
  BASKETBALL(SPORT),
  RUNNING(SPORT),
  NIGHTCLUB(ENTERTAINMENT),
  CINEMA(ENTERTAINMENT),
  RESTAURANT(ENTERTAINMENT),
  SPEAKING_CLUB(EDUCATION),
  LECTURE(EDUCATION);

  private final ActivityGroup activityGroup;

  public static Map<ActivityGroup, List<ActivityType>> groupedActivity = Arrays.stream(ActivityType.values())
      .collect(Collectors.groupingBy(ActivityType::getActivityGroup));

  ActivityType(ActivityGroup activityGroup) {
    this.activityGroup = activityGroup;
  }
}
