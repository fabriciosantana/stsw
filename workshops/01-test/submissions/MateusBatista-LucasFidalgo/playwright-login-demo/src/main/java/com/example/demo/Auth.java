package com.example.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public class Auth {

  public static String createToken(String secret, String subject, int expiresInMinutes) {
    var now = Instant.now();
    var exp = now.plusSeconds(expiresInMinutes * 60L);
    return JWT.create()
        .withSubject(subject)
        .withIssuedAt(Date.from(now))
        .withExpiresAt(Date.from(exp))
        .sign(Algorithm.HMAC256(secret));
  }

  public static Optional<String> verifyAndGetSubject(String secret, String token) {
    try {
      DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
      return Optional.ofNullable(jwt.getSubject());
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
