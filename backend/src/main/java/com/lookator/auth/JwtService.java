package com.lookator.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class JwtService {
    private final String issuer;
    private final Algorithm algorithm;
    private final long expiresMinutes;

    public JwtService(@Value("${lookator.jwt.issuer}") String issuer,
                      @Value("${lookator.jwt.secret}") String secret,
                      @Value("${lookator.jwt.expires-minutes}") long expiresMinutes) {
        this.issuer = issuer;
        this.algorithm = Algorithm.HMAC256(secret);
        this.expiresMinutes = expiresMinutes;
    }

    public String createToken(AppUser user) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getId().toString())
                .withClaim("email", user.getEmail())
                .withExpiresAt(Instant.now().plus(expiresMinutes, ChronoUnit.MINUTES))
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) {
        return JWT.require(algorithm).withIssuer(issuer).build().verify(token);
    }

    public UUID userId(String token) {
        return UUID.fromString(verify(token).getSubject());
    }
}
