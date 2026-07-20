package com.heshant.mapstruct.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    // Different from Entity
    private String email;

    // Different from Entity
    private String phoneNo;
}