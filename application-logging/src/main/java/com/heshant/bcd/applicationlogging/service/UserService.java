package com.heshant.bcd.applicationlogging.service;

import com.heshant.bcd.applicationlogging.dto.UserRequestDto;
import com.heshant.bcd.applicationlogging.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);

    void deleteUser(Long id);
}
