package backend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter
extends OncePerRequestFilter {


private final JwtService jwtService;

public JwtAuthenticationFilter(
        JwtService jwtService) {

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

    String token =
            extractTokenFromCookie(request);

    if (token == null) {
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

    } catch (Exception e) {

        SecurityContextHolder
                .clearContext();
    }

    filterChain.doFilter(
            request,
            response
    );
}

private String extractTokenFromCookie(
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
