package com.heshant.mapstruct.service;

import com.heshant.mapstruct.dto.UserDTO;
import com.heshant.mapstruct.entity.User;
import com.heshant.mapstruct.mapper.UserMapper;
import com.heshant.mapstruct.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDTO add(UserDTO dto) {
        User saved = userRepository.save(userMapper.toEntity(dto));
        return userMapper.toDto(saved);
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toDto(user);
    }

    public List<UserDTO> findAll() {
        return userMapper.toDtos(userRepository.findAll());
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}