package com.example.orderemanagement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.orderemanagement.dto.AddInventoryRequestDto;
import com.example.orderemanagement.dto.AddProductRequestDto;
import com.example.orderemanagement.dto.BuyProductRequestDto;
import com.example.orderemanagement.dto.ProductResponseDto;
import com.example.orderemanagement.dto.UpdateProductRequestDto;
import com.example.orderemanagement.mapper.InventoryMapper;
import com.example.orderemanagement.mapper.ProductMapper;
import com.example.orderemanagement.model.Inventory;
import com.example.orderemanagement.model.Product;
import com.example.orderemanagement.repository.InventoryRepository;
import com.example.orderemanagement.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public Product addProduct(AddProductRequestDto requestDto) {
        if (productRepository.findByName(requestDto.getName()).isPresent())
            throw new RuntimeException("Product with this name already exists");
        Product product = ProductMapper.toEntity(requestDto);
        productRepository.save(product);
        inventoryRepository.save(InventoryMapper.toEntity(product, requestDto));
        return product;
    }

    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> {
            ProductResponseDto dto = ProductMapper.toResponseDto(product);
            InventoryMapper.toResponseDto(inventoryRepository.findByProduct(product)
                                          .orElseThrow(() -> new RuntimeException("Inventory not found"))
                                          , dto);
            return dto;
        }).collect(Collectors.toList());
    }

    public ProductResponseDto getProductByName(String name) {
        Product product = productRepository.findByName(name)
                                          .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductResponseDto dto = ProductMapper.toResponseDto(product);
        InventoryMapper.toResponseDto(inventoryRepository.findByProduct(product)
                                          .orElseThrow(() -> new RuntimeException("Inventory not found"))
                                          , dto);
        return dto;
    }

    @Transactional
    public void deleteProduct(Long id, String name) {
        Product product = resolveProduct(id, name);
        inventoryRepository.deleteById(product.getId());
        productRepository.delete(product);
    }


    @Transactional
    public ProductResponseDto updateProduct(UpdateProductRequestDto requestDto, Long id, String name) {
        Product product = resolveProduct(id, name);
        if (requestDto.getNewName() != null)
            product.setName(requestDto.getNewName());
        if (requestDto.getNewDescription() != null)
            product.setDescription(requestDto.getNewDescription());
        if (requestDto.getNewPrice() != null)
            product.setPrice(Double.parseDouble(requestDto.getNewPrice()));
        productRepository.save(product);
        ProductResponseDto responseDto = ProductMapper.toResponseDto(product);
        return InventoryMapper.toResponseDto(inventoryRepository.findByProduct(product)
                                          .orElseThrow(() -> new RuntimeException("Inventory not found"))
                                          , responseDto);
    }
    

    @Transactional
    public ProductResponseDto addToInventory(AddInventoryRequestDto requestDto, Long id, String name) {
        Product product = resolveProduct(id, name);
        Inventory inventory = inventoryRepository.findByProduct(product)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        inventory.setStockQuantity(inventory.getStockQuantity() + requestDto.getQuantityToAdd());
        inventoryRepository.save(inventory);
        ProductResponseDto responseDto = ProductMapper.toResponseDto(product);
        return InventoryMapper.toResponseDto(inventory, responseDto);
    }
    

    @Transactional
    public void buyProduct(BuyProductRequestDto requestDto, Long id, String name) {
        Product product = resolveProduct(id, name);
        Inventory inventory = inventoryRepository.findByProduct(product)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        if (inventory.getStockQuantity() < requestDto.getQuantity())
            throw new RuntimeException("Insufficient stock");
        inventory.setStockQuantity(inventory.getStockQuantity() - requestDto.getQuantity());
        inventoryRepository.save(inventory);
    }
    
    private Product resolveProduct(Long id, String name) {
        if (id == null && (name == null || name.isBlank()))
            throw new RuntimeException("Either product ID or name must be provided.");
        Product product;
        if (id != null) {
            product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        }
        else {
            product = productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        }
        if (id != null && name != null) {
            if (product.getName().equals(name) && product.getId().equals(id)) 
                return product;
            else
                throw new RuntimeException("Conflicting product ID and name");
        }
        return product;
    }
}
