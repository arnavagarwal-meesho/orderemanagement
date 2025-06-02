package com.example.orderemanagement.mapper;

import com.example.orderemanagement.dto.AddProductRequestDto;
import com.example.orderemanagement.dto.ProductResponseDto;
import com.example.orderemanagement.model.Inventory;
import com.example.orderemanagement.model.Product;

public class InventoryMapper{
    public static Inventory toEntity(Product product, AddProductRequestDto requestDto) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setStockQuantity(requestDto.getInitialStock());
        return inventory;
    }

    public static ProductResponseDto toResponseDto(Inventory inventory, ProductResponseDto productResponseDto) {
        productResponseDto.setStockQuantity(inventory.getStockQuantity());
        return productResponseDto;
    }
}
