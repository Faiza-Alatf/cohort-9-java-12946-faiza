package backend.controller;

import backend.dto.AuthResponse;
import backend.dto.ChangePasswordRequest;
import backend.dto.LoginRequest;
import backend.dto.RegisterRequest;
import backend.entity.User;
import backend.security.JwtService;
import backend.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
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
        @Valid @RequestBody RegisterRequest request) {

    log.info("Registration API request received");

    User user = authService.register(request);

    String identifier = user.getEmail() != null
            ? user.getEmail()
            : user.getPhone();

    String token = jwtService.generateToken(identifier);

    log.info("Registration API completed successfully");

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(toAuthResponse(user, token));
}

// Login API
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(
        @Valid @RequestBody LoginRequest request) {

    log.info("Login API request received");

    User user = authService.login(request);

    String identifier = user.getEmail() != null
            ? user.getEmail()
            : user.getPhone();

    String token = jwtService.generateToken(identifier);

    log.info("Login API completed successfully");

    return ResponseEntity.ok(
            toAuthResponse(user, token)
    );
}

// Change Password API
// Requires a valid JWT token
@PutMapping("/change-password")
public ResponseEntity<Void> changePassword(
        @Valid @RequestBody ChangePasswordRequest request,
        java.security.Principal principal) {

    log.info("Change password API request received");

    authService.changePassword(
            principal.getName(),
            request
    );

    log.info("Change password API completed successfully");

    return ResponseEntity.noContent().build();
}

private AuthResponse toAuthResponse(
        User user,
        String token) {

    return new AuthResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhone(),
            token
    );
}


}
