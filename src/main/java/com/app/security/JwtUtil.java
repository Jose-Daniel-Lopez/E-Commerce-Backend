package com.app.security;

import com.app.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for generating, parsing, and validating JWT tokens.
 * Uses JJWT library with HS512 algorithm.
 */
@Component
public class JwtUtil {

    private static final long JWT_EXPIRATION_MS = 86_400_000; // 24 hours

    private static final String CLAIM_KEY_ROLE = "role";
    private static final String CLAIM_KEY_USER_ID = "userId";

    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final Key signingKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret must be configured");
        }
        // Create HMAC key suitable for HS512
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for the given user.
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_ROLE, user.getRole().name());
        claims.put(CLAIM_KEY_USER_ID, user.getId());
        return createToken(claims, user.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + JWT_EXPIRATION_MS))
                .signWith(signingKey, SignatureAlgorithm.HS512)  // Explicit algorithm
                .compact();
    }

    /**
     * Validates token against the given user (checks username and expiration).
     */
    public boolean validateToken(String token, User user) {
        if (token == null || user == null) return false;
        final String username = getUsernameFromToken(token);
        return username != null && username.equals(user.getEmail()) && !isTokenExpired(token);
    }

    /**
     * Extracts username (subject) from token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts expiration date from token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from token.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            // Even if expired, we can still read claims
            return claimsResolver.apply(e.getClaims());
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Parses the full claims from the JWT token.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey)  // This works in JJWT 0.11.x
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the token is expired.
     */
    private boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration != null && expiration.before(new Date());
    }

    public String extractUsername(String jwtToken) {
        try {
            return getClaimFromToken(jwtToken, Claims::getSubject);
        } catch (JwtException e) {
            logger.warn("Failed to extract username from JWT token: {}", e.getMessage());
            return null;
        }
    }
}