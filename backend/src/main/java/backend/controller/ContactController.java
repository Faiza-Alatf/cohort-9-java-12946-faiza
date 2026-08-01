package backend.controller;

import backend.dto.ContactRequest;
import backend.dto.ContactResponse;
import backend.service.ContactService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class ContactController {


private static final Logger log =
        LoggerFactory.getLogger(ContactController.class);

private final ContactService contactService;

public ContactController(ContactService contactService) {
    this.contactService = contactService;
}

// Create a new contact
@PostMapping
public ResponseEntity<ContactResponse> createContact(
        @Valid @RequestBody ContactRequest request,
        Authentication authentication) {

    log.info("Create contact API request received");

    String userEmail = authentication.getName();

    ContactResponse response =
            contactService.createContact(request, userEmail);

    log.info(
            "Contact created successfully. Contact ID: {}",
            response.getId()
    );

    log.info("Create contact API completed successfully");

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
}

// Get all contacts with pagination and search
@GetMapping
public ResponseEntity<Page<ContactResponse>> getContacts(
        @RequestParam(defaultValue = "") String search,
        @RequestParam(defaultValue = "0")
        @Min(value = 0, message = "Page must be 0 or greater")
        int page,
        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "Size must be at least 1")
        @Max(value = 100, message = "Size must not exceed 100")
        int size,
        Authentication authentication) {

    log.info(
            "Get contacts API request received. Page: {}, Size: {}",
            page,
            size
    );

    String userEmail = authentication.getName();

    Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("firstName").ascending()
    );

    Page<ContactResponse> contacts =
            contactService.getContacts(
                    userEmail,
                    search,
                    pageable
            );

    log.info(
            "Get contacts API completed successfully. Page: {}, Size: {}",
            page,
            size
    );

    return ResponseEntity.ok(contacts);
}

// Get contact by ID
@GetMapping("/{id}")
public ResponseEntity<ContactResponse> getContactById(
        @PathVariable Long id,
        Authentication authentication) {

    log.info(
            "Get contact by ID API request received. Contact ID: {}",
            id
    );

    String userEmail = authentication.getName();

    ContactResponse response =
            contactService.getContactById(
                    id,
                    userEmail
            );

    log.info(
            "Get contact by ID API completed successfully. Contact ID: {}",
            id
    );

    return ResponseEntity.ok(response);
}

// Update contact
@PutMapping("/{id}")
public ResponseEntity<ContactResponse> updateContact(
        @PathVariable Long id,
        @Valid @RequestBody ContactRequest request,
        Authentication authentication) {

    log.info(
            "Update contact API request received. Contact ID: {}",
            id
    );

    String userEmail = authentication.getName();

    ContactResponse response =
            contactService.updateContact(
                    id,
                    request,
                    userEmail
            );

    log.info(
            "Update contact API completed successfully. Contact ID: {}",
            id
    );

    return ResponseEntity.ok(response);
}

// Delete contact
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteContact(
        @PathVariable Long id,
        Authentication authentication) {

    log.info(
            "Delete contact API request received. Contact ID: {}",
            id
    );

    String userEmail = authentication.getName();

    contactService.deleteContact(
            id,
            userEmail
    );

    log.info(
            "Delete contact API completed successfully. Contact ID: {}",
            id
    );

    return ResponseEntity.noContent().build();
}


}
