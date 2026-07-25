package backend.service;

import backend.dto.LoginRequest;
import backend.dto.RegisterRequest;
import backend.entity.User;
import backend.exception.DuplicateResourceException;
import backend.exception.InvalidCredentialsException;
import backend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // User Registration
    public User register(RegisterRequest request) {

        // Normalize email and phone
        String email = normalizeEmail(request.getEmail());
        String phone = normalizePhone(request.getPhone());

        // Check that email or phone is provided
        if (email == null && phone == null) {
            throw new IllegalArgumentException(
                    "Either email or phone number is required"
            );
        }

        // Check duplicate email
        if (email != null && userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Email is already registered"
            );
        }

        // Check duplicate phone
        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new DuplicateResourceException(
                    "Phone number is already registered"
            );
        }

        // Create new user
        User user = new User();

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setPhone(phone);

        // Hash password before saving
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        try {
            return userRepository.save(user);

        } catch (DataIntegrityViolationException ex) {

            // Re-throw the original exception.
            // Database-specific handling is done centrally
            // in GlobalExceptionHandler.
            throw ex;
        }
    }

    // User Login
    public User login(LoginRequest request) {

        // Normalize login identifier
        String identifier = request.getIdentifier().trim();

        User user;

        // Check if identifier is email
        if (identifier.contains("@")) {

            String email = identifier.toLowerCase();

            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new InvalidCredentialsException(
                            "Invalid email or password"
                    ));

        } else {

            // Otherwise treat identifier as phone
            String phone = identifier;

            user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new InvalidCredentialsException(
                            "Invalid phone or password"
                    ));
        }

        // Verify password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new InvalidCredentialsException(
                    "Invalid email/phone or password"
            );
        }

        return user;
    }

    // Normalize email
    private String normalizeEmail(String email) {

        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    // Normalize phone
    private String normalizePhone(String phone) {

        if (phone == null || phone.isBlank()) {
            return null;
        }

        return phone.trim();
    }
}