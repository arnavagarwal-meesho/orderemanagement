package com.example.orderemanagement.mapper;

import com.example.orderemanagement.dto.AddProductRequestDto;
import com.example.orderemanagement.dto.ProductResponseDto;
import com.example.orderemanagement.model.Product;

public class ProductMapper {
    public static Product toEntity(AddProductRequestDto requestDto) {
        Product product = new Product();
        product.setName(requestDto.getName());
        product.setDescription(requestDto.getDescription());
        product.setPrice(requestDto.getPrice());
        return product;
    }

    public static ProductResponseDto toResponseDto(Product product) {
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setId(product.getId());
        responseDto.setName(product.getName());
        responseDto.setDescription(product.getDescription());
        responseDto.setPrice(product.getPrice());
        return responseDto;
    }
}
