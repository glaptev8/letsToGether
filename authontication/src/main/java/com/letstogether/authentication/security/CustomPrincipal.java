package com.letstogether.authentication.security;

import java.security.Principal;

public record CustomPrincipal(Long id,
                              String email) implements Principal {

  @Override
  public String getName() {
    return email();
  }
}