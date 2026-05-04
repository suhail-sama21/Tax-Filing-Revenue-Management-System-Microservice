package com.cognizant.auditservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class AuthUtil {

    private static final String SECRET = "mysecretkeymysecretkeymysecretkeymysecretkey";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // Generate JWT Token (Updated for 0.13.0)
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .claim("role", role)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(getSigningKey(), Jwts.SIG.HS256) // Explicitly set the algorithm
                .compact();
    }

    // Extract username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract role
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Extract expiration
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract specific claim
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // Extract all claims (Updated for 0.13.0)
    private Claims extractAllClaims(String token) {
        return Jwts.parser() // parserBuilder() is now just parser()
                .verifyWith(getSigningKey()) // setSigningKey is replaced by verifyWith
                .build()
                .parseSignedClaims(token) // parseClaimsJws is now parseSignedClaims
                .getPayload(); // getBody() is now getPayload()
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token) {
        final String username = extractUsername(token);
        return  !isTokenExpired(token);
    }
}