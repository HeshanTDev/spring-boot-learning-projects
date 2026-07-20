package com.heshant.mapstruct.service;

import com.heshant.mapstruct.dto.ContactDTO;
import com.heshant.mapstruct.entity.Contact;
import com.heshant.mapstruct.mapper.ContactMapper;
import com.heshant.mapstruct.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    // Add Contact
    public ContactDTO addContact(ContactDTO dto) {

        Contact contact = new Contact();
        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setEmail(dto.getEmail());
        contact.setPhoneNo(dto.getPhoneNo());

        Contact savedContact = contactRepository.save(contact);

        return contactMapper.toDto(savedContact);
    }

    // Find Contact By Id
    public ContactDTO findById(Long id) {

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        return contactMapper.toDto(contact);
    }

    // Find All Contacts
    public List<ContactDTO> findAll() {

        List<Contact> contacts = contactRepository.findAll();

//        return contacts.stream()
//                .map(contact -> ContactDTO.builder()
//                        .id(contact.getId())
//                        .firstName(contact.getFirstName())
//                        .lastName(contact.getLastName())
//                        .email(contact.getEmail())
//                        .phoneNo(contact.getPhoneNo())
//                        .build())
//                .toList();

        return contactMapper.toDtos(contacts);
    }

    // Delete Contact
    public void delete(Long id) {

        if (!contactRepository.existsById(id)) {
            throw new RuntimeException("Contact not found");
        }

        contactRepository.deleteById(id);
    }
}