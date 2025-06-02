package com.example.orderemanagement.dto;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private double price;
    private String description;
    private int stockQuantity;    
}
