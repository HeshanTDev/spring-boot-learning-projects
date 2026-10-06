package com.heshant.bcd.applicationlogging.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(
        @NotBlank(message = "User name must not be blank")
        String name,

        @NotBlank(message = "User email must not be blank")
        @Email(message = "User email must be a valid email address")
        String email
) {
}
