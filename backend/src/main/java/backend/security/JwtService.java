package backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey signingKey;

    // Token validity: 24 hours
    private static final long EXPIRATION_TIME =
            1000 * 60 * 60 * 24;

    // Validate and create signing key when application starts
    @PostConstruct
    public void init() {

        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);

            signingKey = Keys.hmacShaKeyFor(keyBytes);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Invalid JWT secret configuration. " +
                    "Make sure jwt.secret is a valid Base64-encoded " +
                    "secret with at least 256 bits.",
                    e
            );
        }
    }

    // Generate JWT token
    public String generateToken(String identifier) {

        return Jwts.builder()
                .subject(identifier)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )
                .signWith(signingKey)
                .compact();
    }

    // Parse and validate JWT token once
    public Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extract identifier from already parsed claims
    public String extractIdentifier(Claims claims) {

        return claims.getSubject();
    }

    // Check whether token is valid
    public boolean isTokenValid(String token) {

        try {
            getClaims(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}