package com.example.orderemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddProductRequestDto {
    @NotBlank(message = "Name is required")
    String name;
    String description;
    @NotNull(message = "Price is required")
    Double price;
    @NotNull(message = "Initial stock is required")
    Integer initialStock;
}
