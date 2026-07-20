package com.heshant.mapstruct.mapper;

import com.heshant.mapstruct.dto.UserDTO;
import com.heshant.mapstruct.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "emailId" , target = "email")
    @Mapping(source = "mobileNumber" , target = "phoneNo")
    UserDTO toDto(User user);

    @Mapping(target = "emailId" , source = "email")
    @Mapping(target = "mobileNumber" , source = "phoneNo")
    User toEntity(UserDTO dto);

    List<UserDTO> toDtos(List<User> users);
}