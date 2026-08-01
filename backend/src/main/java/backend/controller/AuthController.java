package backend.controller;

import backend.dto.AuthResponse;
import backend.dto.ChangePasswordRequest;
import backend.dto.LoginRequest;
import backend.dto.RegisterRequest;
import backend.entity.User;
import backend.security.JwtService;
import backend.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
origins = "http://localhost:5173",
allowCredentials = "true"
)
public class AuthController {


private static final Logger log =
        LoggerFactory.getLogger(AuthController.class);

private final AuthService authService;
private final JwtService jwtService;

public AuthController(
        AuthService authService,
        JwtService jwtService) {

    this.authService = authService;
    this.jwtService = jwtService;
}

// Registration API
@PostMapping("/register")
public ResponseEntity<AuthResponse> register(
        @Valid @RequestBody RegisterRequest request,
        HttpServletResponse response) {

    log.info("Registration API request received");

    User user = authService.register(request);

    String identifier = user.getEmail() != null
            ? user.getEmail()
            : user.getPhone();

    String token =
            jwtService.generateToken(identifier);

    addJwtCookie(response, token);

    log.info(
            "Registration API completed successfully"
    );

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(toAuthResponse(user));
}

// Login API
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(
        @Valid @RequestBody LoginRequest request,
        HttpServletResponse response) {

    log.info("Login API request received");

    User user = authService.login(request);

    String identifier = user.getEmail() != null
            ? user.getEmail()
            : user.getPhone();

    String token =
            jwtService.generateToken(identifier);

    addJwtCookie(response, token);

    log.info(
            "Login API completed successfully"
    );

    return ResponseEntity.ok(
            toAuthResponse(user)
    );
}

// Change Password API
// Requires a valid JWT token
@PutMapping("/change-password")
public ResponseEntity<Void> changePassword(
        @Valid @RequestBody ChangePasswordRequest request,
        java.security.Principal principal) {

    log.info(
            "Change password API request received"
    );

    authService.changePassword(
            principal.getName(),
            request
    );

    log.info(
            "Change password API completed successfully"
    );

    return ResponseEntity.noContent().build();
}

// Logout API
@PostMapping("/logout")
public ResponseEntity<Void> logout(
        HttpServletResponse response) {

    log.info("Logout API request received");

    Cookie cookie = new Cookie(
            "jwt",
            ""
    );

    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath("/");
    cookie.setMaxAge(0);

    response.addCookie(cookie);

    log.info(
            "Logout API completed successfully"
    );

    return ResponseEntity.noContent().build();
}

private void addJwtCookie(
        HttpServletResponse response,
        String token) {

    Cookie cookie = new Cookie(
            "jwt",
            token
    );

    // JavaScript cannot access this cookie
    cookie.setHttpOnly(true);

    // For localhost development
    cookie.setSecure(false);

    cookie.setPath("/");

    // Cookie lifetime: 1 day
    cookie.setMaxAge(24 * 60 * 60);

    response.addCookie(cookie);
}

private AuthResponse toAuthResponse(
        User user) {

    return new AuthResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhone(),
            null
    );
}


}
