package com.heshant.bcd.applicationlogging.service.impl;

import com.heshant.bcd.applicationlogging.dto.UserRequestDto;
import com.heshant.bcd.applicationlogging.dto.UserResponseDto;
import com.heshant.bcd.applicationlogging.entity.User;
import com.heshant.bcd.applicationlogging.exception.ResourceNotFoundException;
import com.heshant.bcd.applicationlogging.repository.UserRepository;
import com.heshant.bcd.applicationlogging.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        log.info("Creating user: {}", userRequestDto.name());

        User user = new User();
        user.setName(userRequestDto.name());
        user.setEmail(userRequestDto.email());

        User savedUser = userRepository.save(user);
        log.info("User created with id: {}", savedUser.getId());

        return toResponse(savedUser);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        return toResponse(findUser(id));
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        log.info("Updating user with id: {}", id);

        User user = findUser(id);
        user.setName(userRequestDto.name());
        user.setEmail(userRequestDto.email());

        User savedUser = userRepository.save(user);
        log.info("User updated with id: {}", savedUser.getId());

        return toResponse(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        User user = findUser(id);
        userRepository.delete(user);

        log.info("User deleted with id: {}", id);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
    }

    private UserResponseDto toResponse(User user) {
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail());
    }
}
