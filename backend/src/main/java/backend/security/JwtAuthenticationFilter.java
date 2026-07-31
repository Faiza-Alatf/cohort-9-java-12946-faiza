package backend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

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

        String authHeader = request.getHeader("Authorization");

        System.out.println("JWT Filter - Request: "
                + request.getMethod()
                + " "
                + request.getRequestURI());

        System.out.println("JWT Filter - Authorization Header Present: "
                + (authHeader != null));

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            System.out.println(
                    "JWT Filter - No valid Bearer token found"
            );

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            Claims claims = jwtService.getClaims(token);

            String identifier =
                    jwtService.extractIdentifier(claims);

            System.out.println(
                    "JWT Filter - Token Valid for: "
                            + identifier
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
                    .setAuthentication(authentication);

            System.out.println(
                    "JWT Filter - Authentication Set Successfully"
            );

        } catch (Exception e) {

            System.out.println(
                    "JWT Filter - Token Validation FAILED: "
                            + e.getClass().getSimpleName()
            );

            System.out.println(
                    "JWT Filter - Error: "
                            + e.getMessage()
            );

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}