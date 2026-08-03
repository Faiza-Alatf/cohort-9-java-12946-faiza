package backend.controller;

import backend.dto.ContactRequest;
import backend.dto.ContactResponse;
import backend.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "http://localhost:5173")
public class ContactController {

private static final int MAX_PAGE_SIZE = 100;

private final ContactService contactService;

public ContactController(ContactService contactService) {
    this.contactService = contactService;
}

// Create a new contact
@PostMapping
public ResponseEntity<ContactResponse> createContact(
        @Valid @RequestBody ContactRequest request,
        Authentication authentication) {

    String userEmail = authentication.getName();

    ContactResponse response =
            contactService.createContact(request, userEmail);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
}

// Get all contacts with pagination and search
@GetMapping
public ResponseEntity<Page<ContactResponse>> getContacts(
        @RequestParam(defaultValue = "") String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Authentication authentication) {

    String userEmail = authentication.getName();

    int safePageSize = Math.min(size, MAX_PAGE_SIZE);

    Pageable pageable = PageRequest.of(
            page,
            safePageSize,
            Sort.by("firstName").ascending()
    );

    Page<ContactResponse> contacts =
            contactService.getContacts(
                    userEmail,
                    search,
                    pageable
            );

    return ResponseEntity.ok(contacts);
}

// Get contact by ID
@GetMapping("/{id}")
public ResponseEntity<ContactResponse> getContactById(
        @PathVariable Long id,
        Authentication authentication) {

    String userEmail = authentication.getName();

    ContactResponse response =
            contactService.getContactById(
                    id,
                    userEmail
            );

    return ResponseEntity.ok(response);
}

// Update contact
@PutMapping("/{id}")
public ResponseEntity<ContactResponse> updateContact(
        @PathVariable Long id,
        @Valid @RequestBody ContactRequest request,
        Authentication authentication) {

    String userEmail = authentication.getName();

    ContactResponse response =
            contactService.updateContact(
                    id,
                    request,
                    userEmail
            );

    return ResponseEntity.ok(response);
}

// Delete contact
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteContact(
        @PathVariable Long id,
        Authentication authentication) {

    String userEmail = authentication.getName();

    contactService.deleteContact(
            id,
            userEmail
    );

    return ResponseEntity.noContent().build();
}

}
