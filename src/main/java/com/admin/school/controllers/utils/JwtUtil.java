package com.admin.school.controllers.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static SecretKey secretKey ;

    // Generate a JWT token
    public static String generateToken(Map<String, Object> claims) {
        String s = Jwts.builder()
                .setSubject(claims.get("authorId").toString())
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        String userId = extractUserId(s);
        return s;
    }

    private static String extractUserId(String token) {
        String s = extractClaim(token, Claims::getSubject);
        return s;
    }

    private static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    private static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static Boolean validateToken(String token, String userId) {
        final String id = extractUserId(token);
        return (userId.equals(id) && !isTokenExpired(token));
    }

    public static void setSecretKey(SecretKey secretKey) {
        JwtUtil.secretKey = secretKey;
    }
}
