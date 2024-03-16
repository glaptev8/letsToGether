package com.letstogether.authentication.security;

import io.jsonwebtoken.Claims;

public record VerificationResult(Claims claims,
                                 String token) {
}
