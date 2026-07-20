package com.heshant.mapstruct.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "order_details")
@ToString
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private float amount;
    private String description;
    private LocalDate date;
}
