package org.limckmy.account.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    private final KeyPair keyPair;

    public JWTUtil(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuer("org.limckmy")
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // hardcode to 3600000ms (1hr) , temporary to 10 sec to verify token expired scenario
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
