package backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


private static final Logger log =
        LoggerFactory.getLogger(JwtAuthenticationFilter.class);

private final JwtService jwtService;

public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
}

@Override
protected boolean shouldNotFilter(
        HttpServletRequest request) {

    String path = request.getServletPath();

    return path.equals("/api/auth/register")
            || path.equals("/api/auth/login");
}

@Override
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

    // JWT is stored in an HttpOnly cookie
    String token = extractJwtFromCookie(request);

    // No JWT cookie found
    if (token == null || token.isBlank()) {

        filterChain.doFilter(
                request,
                response
        );

        return;
    }

    try {

        Claims claims =
                jwtService.getClaims(token);

        String identifier =
                jwtService.extractIdentifier(
                        claims
                );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        identifier,
                        null,
                        Collections.emptyList()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        authentication
                );

    } catch (JwtException e) {

        log.warn(
                "JWT validation failed: {}",
                e.getClass().getSimpleName()
        );

        SecurityContextHolder
                .clearContext();

    } catch (IllegalArgumentException e) {

        log.warn(
                "Invalid JWT token: {}",
                e.getClass().getSimpleName()
        );

        SecurityContextHolder
                .clearContext();
    }

    filterChain.doFilter(
            request,
            response
    );
}

// Extract JWT from the HttpOnly cookie
private String extractJwtFromCookie(
        HttpServletRequest request) {

    Cookie[] cookies =
            request.getCookies();

    if (cookies == null) {
        return null;
    }

    for (Cookie cookie : cookies) {

        if ("jwt".equals(cookie.getName())) {
            return cookie.getValue();
        }
    }

    return null;
}


}
