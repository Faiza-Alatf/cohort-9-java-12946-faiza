package backend.service;

import backend.dto.ChangePasswordRequest;
import backend.dto.LoginRequest;
import backend.dto.RegisterRequest;
import backend.entity.User;
import backend.exception.DuplicateResourceException;
import backend.exception.InvalidCredentialsException;
import backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


private static final Logger log =
        LoggerFactory.getLogger(AuthService.class);

private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;

public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
}

public User register(RegisterRequest request) {

    log.info("User registration attempt started");

    String email = normalizeEmail(request.getEmail());
    String phone = normalizePhone(request.getPhone());

    if (email == null && phone == null) {
        log.warn("Registration failed: neither email nor phone was provided");

        throw new IllegalArgumentException(
                "Either email or phone number is required"
        );
    }

    if (email != null && userRepository.existsByEmail(email)) {
        log.warn("Registration failed: email already registered");

        throw new DuplicateResourceException(
                "Email is already registered"
        );
    }

    if (phone != null && userRepository.existsByPhone(phone)) {
        log.warn("Registration failed: phone number already registered");

        throw new DuplicateResourceException(
                "Phone number is already registered"
        );
    }

    User user = new User();

    user.setFirstName(request.getFirstName().trim());
    user.setLastName(request.getLastName().trim());
    user.setEmail(email);
    user.setPhone(phone);

    user.setPassword(
            passwordEncoder.encode(request.getPassword())
    );

    try {

        User savedUser = userRepository.save(user);

        log.info("User registration successful");

        return savedUser;

    } catch (DataIntegrityViolationException ex) {

        log.error(
                "Database integrity violation occurred during user registration",
                ex
        );

        throw ex;
    }
}

public User login(LoginRequest request) {

    log.info("User login attempt started");

    String identifier = request.getIdentifier().trim();

    User user;

    if (identifier.contains("@")) {

        String email = identifier.toLowerCase();

        user = userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    log.warn("Login failed: user not found");

                    return new InvalidCredentialsException(
                            "Invalid email or password"
                    );
                });

    } else {

        String phone = identifier;

        user = userRepository.findByPhone(phone)
                .orElseThrow(() -> {

                    log.warn("Login failed: user not found");

                    return new InvalidCredentialsException(
                            "Invalid phone or password"
                    );
                });
    }

    if (!passwordEncoder.matches(
            request.getPassword(),
            user.getPassword())) {

        log.warn("Login failed: invalid password");

        throw new InvalidCredentialsException(
                "Invalid email/phone or password"
        );
    }

    log.info("User login successful");

    return user;
}

public void changePassword(
        String userEmail,
        ChangePasswordRequest request) {

    log.info("Password change attempt started");

    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> {

                log.warn(
                        "Password change failed: authenticated user not found"
                );

                return new InvalidCredentialsException(
                        "Authenticated user not found"
                );
            });

    if (!passwordEncoder.matches(
            request.getCurrentPassword(),
            user.getPassword())) {

        log.warn(
                "Password change failed: current password is incorrect"
        );

        throw new InvalidCredentialsException(
                "Current password is incorrect"
        );
    }

    if (passwordEncoder.matches(
            request.getNewPassword(),
            user.getPassword())) {

        log.warn(
                "Password change failed: new password is same as current password"
        );

        throw new IllegalArgumentException(
                "New password must be different from current password"
        );
    }

    user.setPassword(
            passwordEncoder.encode(
                    request.getNewPassword()
            )
    );

    userRepository.save(user);

    log.info("Password changed successfully");
}

private String normalizeEmail(String email) {

    if (email == null || email.isBlank()) {
        return null;
    }

    return email.trim().toLowerCase();
}

private String normalizePhone(String phone) {

    if (phone == null || phone.isBlank()) {
        return null;
    }

    return phone.trim();
}

}
