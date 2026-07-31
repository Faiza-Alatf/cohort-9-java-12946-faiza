package backend.service;

import backend.dto.ContactRequest;
import backend.dto.ContactResponse;
import backend.entity.Contact;
import backend.entity.User;
import backend.exception.ResourceNotFoundException;
import backend.exception.UnauthorizedException;
import backend.repository.ContactRepository;
import backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(
            ContactRepository contactRepository,
            UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ContactResponse createContact(
            ContactRequest request,
            String userEmail) {

        User user = getUserByEmail(userEmail);

        Contact contact = new Contact();

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setTitle(request.getTitle());
        contact.setWorkEmail(request.getWorkEmail());
        contact.setPersonalEmail(request.getPersonalEmail());
        contact.setWorkPhone(request.getWorkPhone());
        contact.setHomePhone(request.getHomePhone());
        contact.setPersonalPhone(request.getPersonalPhone());
        contact.setUser(user);

        Contact savedContact = contactRepository.save(contact);

        return mapToResponse(savedContact);
    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> getContacts(
            String userEmail,
            String search,
            Pageable pageable) {

        User user = getUserByEmail(userEmail);

        Page<Contact> contacts;

        if (search == null || search.trim().isEmpty()) {

            contacts = contactRepository.findByUser(
                    user,
                    pageable
            );

        } else {

            contacts = contactRepository
                    .findByUserAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                            user,
                            search,
                            search,
                            pageable
                    );
        }

        return contacts.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ContactResponse getContactById(
            Long contactId,
            String userEmail) {

        User user = getUserByEmail(userEmail);

        Contact contact = contactRepository
                .findById(contactId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contact not found with id: " + contactId
                        )
                );

        if (!contact.getUser().getId().equals(user.getId())) {

            throw new UnauthorizedException(
                    "You are not authorized to access this contact"
            );
        }

        return mapToResponse(contact);
    }

    @Transactional
    public ContactResponse updateContact(
            Long contactId,
            ContactRequest request,
            String userEmail) {

        User user = getUserByEmail(userEmail);

        Contact contact = contactRepository
                .findById(contactId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contact not found with id: " + contactId
                        )
                );

        if (!contact.getUser().getId().equals(user.getId())) {

            throw new UnauthorizedException(
                    "You are not authorized to update this contact"
            );
        }

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setTitle(request.getTitle());
        contact.setWorkEmail(request.getWorkEmail());
        contact.setPersonalEmail(request.getPersonalEmail());
        contact.setWorkPhone(request.getWorkPhone());
        contact.setHomePhone(request.getHomePhone());
        contact.setPersonalPhone(request.getPersonalPhone());

        Contact updatedContact = contactRepository.save(contact);

        return mapToResponse(updatedContact);
    }

    @Transactional
    public void deleteContact(
            Long contactId,
            String userEmail) {

        User user = getUserByEmail(userEmail);

        Contact contact = contactRepository
                .findById(contactId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contact not found with id: " + contactId
                        )
                );

        if (!contact.getUser().getId().equals(user.getId())) {

            throw new UnauthorizedException(
                    "You are not authorized to delete this contact"
            );
        }

        contactRepository.delete(contact);
    }

    private User getUserByEmail(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        )
                );
    }

    private ContactResponse mapToResponse(Contact contact) {

        return new ContactResponse(
                contact.getId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getTitle(),
                contact.getWorkEmail(),
                contact.getPersonalEmail(),
                contact.getWorkPhone(),
                contact.getHomePhone(),
                contact.getPersonalPhone()
        );
    }
}