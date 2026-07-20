package com.heshant.mapstruct.mapper;

import com.heshant.mapstruct.dto.ContactDTO;
import com.heshant.mapstruct.entity.Contact;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    ContactDTO toDto(Contact contact);

    Contact toEntity(ContactDTO contactDTO);

    List<ContactDTO> toDtos(List<Contact> contacts);
}
