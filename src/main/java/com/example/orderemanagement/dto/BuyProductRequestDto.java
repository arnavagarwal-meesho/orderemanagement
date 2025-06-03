package com.example.orderemanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BuyProductRequestDto {
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    private Long productId;
    private String productName;
    @NotNull(message = "Quantity is required")
    private int quantity;
}
