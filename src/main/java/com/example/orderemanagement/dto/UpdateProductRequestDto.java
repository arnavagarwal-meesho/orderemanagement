package com.example.orderemanagement.dto;

import lombok.Data;

@Data
public class UpdateProductRequestDto {
    private String newName;
    private String newDescription;
    private String newPrice;
}
