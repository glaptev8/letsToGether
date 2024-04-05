package com.letstogether.dto;

import java.util.Set;

public enum HobbyType {
  FOOTBALL,
  BASKETBALL,
  RUNNING,
  HOKEY,
  ART,
  MUSIC,
  DANCE,
  SONG,
  LANGUAGES,
  TRAVELLING,
  YOGA,
  CINEMA,
  RESTAURANTS,
  SHOPPING;

  public static Set<HobbyType> hobbies = Set.of(HobbyType.values());
}
