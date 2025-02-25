package org.dav.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.dav.modals.UserClaim;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey jwtSecret;
    private final Long logoutTime;

    public JwtService(@Value("${token.secret.key}") String jwtSecret,
                      @Value("${token.expiration.ms}") Long logoutTime) {
        this.jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.logoutTime = logoutTime;
    }

    public String generateToken(UserClaim userClaim) {
        return Jwts.builder()
                .subject(userClaim.getEmail()) // Set subject
                .claims(UserClaim.getUserClaims(userClaim)) // Add claims
                .expiration(new Date(System.currentTimeMillis() + logoutTime))
                .issuedAt(new Date())
                .signWith(jwtSecret, Jwts.SIG.HS256)
                .compact();
    }

    public Boolean isTokenValid(String token, UserClaim userClaim) {
        final String email = extractUserName(token);
        return (email.equals(userClaim.getEmail()) && !isTokenExpired(token));
    }

    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
