package backend.config;

import backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


private final JwtAuthenticationFilter jwtAuthenticationFilter;

public SecurityConfig(
        JwtAuthenticationFilter jwtAuthenticationFilter) {

    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

@Bean
public SecurityFilterChain securityFilterChain(
        HttpSecurity http) throws Exception {

    http
            // Enable CORS
            .cors(cors -> cors.configurationSource(
                    corsConfigurationSource()
            ))

            // Disable CSRF
            .csrf(csrf -> csrf.disable())

            // Disable default authentication
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable())

            // JWT is stateless
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                    // Register and Login are public
                    .requestMatchers(
                            "/api/auth/register",
                            "/api/auth/login"
                    ).permitAll()

                    // Everything else requires JWT
                    .anyRequest().authenticated()
            )

            // JWT filter
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

    return http.build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration =
            new CorsConfiguration();

    // React frontend
    configuration.setAllowedOrigins(
            List.of("http://localhost:5173")
    );

    // Allowed HTTP methods
    configuration.setAllowedMethods(
            List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "DELETE",
                    "OPTIONS"
            )
    );

    // Allow all request headers
    configuration.setAllowedHeaders(
            List.of("*")
    );

    // Allow Authorization header / credentials
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration(
            "/**",
            configuration
    );

    return source;
}

}
