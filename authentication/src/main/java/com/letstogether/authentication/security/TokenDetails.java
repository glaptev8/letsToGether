package com.letstogether.authentication.security;

import java.util.Date;

public record TokenDetails(
  Long userId,
  String token,
  Date issuedAt,
  Date expiresAt,
  Boolean isFirstEnter
) {
}
