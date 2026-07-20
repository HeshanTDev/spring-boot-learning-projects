package com.heshant.mapstruct.controller;

import com.heshant.mapstruct.dto.ContactDTO;
import com.heshant.mapstruct.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    // Add Contact
    @PostMapping
    public ResponseEntity<ContactDTO> addContact(@RequestBody ContactDTO contactDTO) {
        ContactDTO savedContact = contactService.addContact(contactDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedContact);
    }

    // Find Contact By Id
    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> findById(@PathVariable Long id) {
        ContactDTO contact = contactService.findById(id);
        return ResponseEntity.ok(contact);
    }

    // Find All Contacts
    @GetMapping
    public ResponseEntity<List<ContactDTO>> findAll() {
        List<ContactDTO> contacts = contactService.findAll();
        return ResponseEntity.ok(contacts);
    }

    // Delete Contact
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contactService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
