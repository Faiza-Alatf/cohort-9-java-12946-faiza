package backend.config;

import backend.security.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// CORS configuration for React Vite frontend
@Bean
public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(
            List.of("http://localhost:5173")
    );

    configuration.setAllowedMethods(
            List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "DELETE",
                    "OPTIONS"
            )
    );

    configuration.setAllowedHeaders(
            List.of(
                    "Authorization",
                    "Content-Type"
            )
    );

    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration(
            "/**",
            configuration
    );

    return source;
}

// Prevent JwtAuthenticationFilter from being
// automatically registered by the servlet container.
// It will only run through Spring Security filter chain.
@Bean
public FilterRegistrationBean<JwtAuthenticationFilter>
jwtFilterRegistration(
        JwtAuthenticationFilter filter) {

    FilterRegistrationBean<JwtAuthenticationFilter> registration =
            new FilterRegistrationBean<>(filter);

    registration.setEnabled(false);

    return registration;
}

@Bean
public SecurityFilterChain securityFilterChain(
        HttpSecurity http)
        throws Exception {

    http
            // Enable CORS
            .cors(cors ->
                    cors.configurationSource(
                            corsConfigurationSource()
                    )
            )

            // Disable CSRF
            .csrf(csrf ->
                    csrf.disable()
            )

            // Disable default username/password authentication
            .httpBasic(httpBasic ->
                    httpBasic.disable()
            )

            .formLogin(formLogin ->
                    formLogin.disable()
            )

            // JWT is stateless
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // Authentication and authorization rules
            .authorizeHttpRequests(auth ->
                    auth

                            // Allow CORS preflight requests
                            .requestMatchers(
                                    HttpMethod.OPTIONS,
                                    "/**"
                            )
                            .permitAll()

                            // Register and Login are public
                            .requestMatchers(
                                    "/api/auth/**"
                            )
                            .permitAll()

                            // Everything else requires JWT
                            .anyRequest()
                            .authenticated()
            )

            // Return 401 for unauthenticated requests
            .exceptionHandling(exception ->
                    exception.authenticationEntryPoint(
                            (request,
                             response,
                             authException) ->
                                    response.sendError(
                                            HttpStatus.UNAUTHORIZED.value(),
                                            "Unauthorized"
                                    )
                    )
            )

            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

    return http.build();
}


}
