package com.lreas.forum.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Claims extractAllClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new MalformedJwtException("Invalid JWT token format");
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

            TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {};
            Map<String, Object> payloadMap = objectMapper.readValue(payloadJson, typeRef);

            return Jwts.claims(payloadMap); // Convert Map to Claims
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT: " + e.getMessage());
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new MalformedJwtException("Invalid JWT token");
        }
        return header.substring(7);
    }

    public String extractEmail(HttpServletRequest request) {
        return extractAllClaims(this.getTokenFromRequest(request)).getSubject(); // Extract "sub"
    }
    public String extractUserId(HttpServletRequest request) {
        return extractAllClaims(this.getTokenFromRequest(request)).get("id", String.class);
    }

    public String extractInstitutionName(HttpServletRequest request) {
        return extractAllClaims(this.getTokenFromRequest(request)).get("institutionName", String.class);
    }
}
