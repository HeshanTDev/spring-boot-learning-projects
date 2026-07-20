package com.heshant.mapstruct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private String orderAmount;   // kept as String for flexibility
    private String description;
    private String orderDate;     // kept as String for easy JSON transfer
}
