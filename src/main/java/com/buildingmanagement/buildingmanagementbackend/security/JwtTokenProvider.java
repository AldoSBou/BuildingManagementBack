package com.buildingmanagement.buildingmanagementbackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys; // Importar Keys para generar la clave
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key; // Importar Key
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationInMs;

    // Generar la clave de firma una sola vez
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                // Usar signWith con la clave generada
                .signWith(getSigningKey())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                // Usar verifyWith con la clave generada y luego build()
                .verifyWith((SecretKey) getSigningKey())
                .build() // Construir el JwtParser
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    // Usar verifyWith con la clave generada y luego build()
                    .verifyWith((SecretKey) getSigningKey())
                    .build() // Construir el JwtParser
                    .parseClaimsJws(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException ex) { // Cambiado a SecurityException de io.jsonwebtoken.security
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
}