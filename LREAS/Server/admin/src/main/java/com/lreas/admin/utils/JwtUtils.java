package com.lreas.admin.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Claims extractAllClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new MalformedJwtException("Invalid JWT token format");
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> payloadMap = objectMapper.readValue(payloadJson, Map.class);

            return Jwts.claims(payloadMap); // Convert Map to Claims
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT: " + e.getMessage());
        }
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject(); // Extract "sub"
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public String extractInstitutionName(String token) {
        return extractAllClaims(token).get("institutionName", String.class);
    }
}
