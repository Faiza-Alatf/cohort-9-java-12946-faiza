package backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


private static final Logger logger =
        LoggerFactory.getLogger(GlobalExceptionHandler.class);

// Contact or User not found
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<Map<String, String>> handleResourceNotFound(
        ResourceNotFoundException ex) {

    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());

    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response);
}

// Unauthorized access to another user's contact
@ExceptionHandler(UnauthorizedException.class)
public ResponseEntity<Map<String, String>> handleUnauthorized(
        UnauthorizedException ex) {

    Map<String, String> response = new HashMap<>();
    response.put("error", ex.getMessage());

    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(response);
}

// Duplicate email or phone detected by application checks
@ExceptionHandler(DuplicateResourceException.class)
public ResponseEntity<Map<String, String>> handleDuplicateResource(
        DuplicateResourceException ex) {

    Map<String, String> response = new HashMap<>();
    response.put(
            "error",
            "Email or phone number is already registered"
    );

    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(response);
}

// Database unique constraint violation
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(
        DataIntegrityViolationException ex) {

    logger.error(
            "Database integrity violation occurred",
            ex
    );

    Map<String, String> response = new HashMap<>();

    String message = ex.getMostSpecificCause()
            .getMessage();

    if (message != null
            && message.contains("uk_users_email")) {

        response.put(
                "error",
                "Email or phone number is already registered"
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    if (message != null
            && message.contains("uk_users_phone")) {

        response.put(
                "error",
                "Email or phone number is already registered"
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    response.put(
            "error",
            "A database error occurred"
    );

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
}

// Invalid login credentials
@ExceptionHandler(InvalidCredentialsException.class)
public ResponseEntity<Map<String, String>> handleInvalidCredentials(
        InvalidCredentialsException ex) {

    Map<String, String> response = new HashMap<>();
    response.put(
            "error",
            "Invalid email/phone or password"
    );

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response);
}

// Validation errors from @Valid
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, String>> handleValidationException(
        MethodArgumentNotValidException ex) {

    Map<String, String> response = new HashMap<>();

    ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .ifPresent(error ->
                    response.put(
                            "error",
                            error.getDefaultMessage()
                    )
            );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
}

// Invalid registration request
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<Map<String, String>> handleIllegalArgument(
        IllegalArgumentException ex) {

    Map<String, String> response = new HashMap<>();
    response.put(
            "error",
            ex.getMessage()
    );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
}

// Preserve status from ResponseStatusException
@ExceptionHandler(ResponseStatusException.class)
public ResponseEntity<Map<String, String>> handleResponseStatusException(
        ResponseStatusException ex) {

    Map<String, String> response = new HashMap<>();
    response.put(
            "error",
            ex.getReason() != null
                    ? ex.getReason()
                    : "Request failed"
    );

    return ResponseEntity
            .status(ex.getStatusCode())
            .body(response);
}

// Preserve status from Spring ErrorResponseException
@ExceptionHandler(ErrorResponseException.class)
public ResponseEntity<Map<String, String>> handleErrorResponseException(
        ErrorResponseException ex) {

    Map<String, String> response = new HashMap<>();
    response.put(
            "error",
            ex.getMessage()
    );

    return ResponseEntity
            .status(ex.getStatusCode())
            .body(response);
}

// Access denied
@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<Map<String, String>> handleAccessDenied(
        AccessDeniedException ex) {

    Map<String, String> response = new HashMap<>();
    response.put(
            "error",
            "Access denied"
    );

    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(response);
}

// Unexpected errors
@ExceptionHandler(Exception.class)
public ResponseEntity<Map<String, String>> handleGeneralException(
        Exception ex) {

    logger.error(
            "Unexpected error occurred while processing request",
            ex
    );

    Map<String, String> response = new HashMap<>();
    response.put(
            "error",
            "An unexpected error occurred"
    );

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
}


}
