package com.example.petstore.models;

import lombok.Data;

@Data
public class Order {
    private Long id;
    private Pet petId;
    private Integer quantity;
    private String shipDate;
    private String status; 
    private Boolean complete;
}
