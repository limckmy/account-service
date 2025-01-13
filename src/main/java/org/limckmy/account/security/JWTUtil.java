package org.limckmy.account.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JWTUtil {

    private final KeyPair keyPair;

    public JWTUtil(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        // TODO - Hardcoded roles for now, to retrieve from DB later under user management
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        claims.put("roles", roles);


        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuer("org.limckmy")
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // hardcode to 3600000ms (1hr) , temporary to 360ms (10 sec) to verify token expired scenario
                .signWith(keyPair.getPrivate())
                .compact();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Token expired");
        } catch (JwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Invalid token");
        }
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public List<GrantedAuthority> getAuthoritiesFromToken(Claims claims) {
        // Assuming the authorities are stored in a claim called "roles"
        List<String> roles = (List<String>) claims.get("roles");

        if (roles != null) {
            // Convert the list of roles into a list of GrantedAuthority objects
            return roles.stream()
                    .map(SimpleGrantedAuthority::new)  // Convert each role to a GrantedAuthority
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();  // Return an empty list if no roles are present
    }
}
