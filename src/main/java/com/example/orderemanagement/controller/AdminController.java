package com.example.orderemanagement.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.orderemanagement.dto.AddInventoryRequestDto;
import com.example.orderemanagement.dto.AddProductRequestDto;
import com.example.orderemanagement.dto.AdminLoginRequestDto;
import com.example.orderemanagement.dto.AdminRequestDto;
import com.example.orderemanagement.dto.AdminResponseDto;
import com.example.orderemanagement.dto.ProductResponseDto;
import com.example.orderemanagement.dto.UpdateProductRequestDto;
import com.example.orderemanagement.model.Product;
import com.example.orderemanagement.service.AdminService;
import com.example.orderemanagement.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final ProductService productService;
    
    @PostMapping("/register")
    public ResponseEntity<AdminResponseDto> createAdmin(@Valid @RequestBody AdminRequestDto requestDto) {
        AdminResponseDto responseDto = adminService.register(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AdminResponseDto> loginAdmin(@Valid @RequestBody AdminLoginRequestDto requestDto) {
        AdminResponseDto responseDto = adminService.login(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/products/add")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody AddProductRequestDto requestDto) {
        Product product = productService.addProduct(requestDto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/product")
    public ResponseEntity<ProductResponseDto> getProductByName(@RequestParam String name) {
        ProductResponseDto responseDto = productService.getProductByName(name);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    

    @DeleteMapping("/product")
    public ResponseEntity<String> deleteProduct(@RequestParam(required = false) Long id, 
                                                @RequestParam(required = false) String name) {
        productService.deleteProduct(id, name);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/product")
    public ResponseEntity<ProductResponseDto> updateProduct(@RequestBody UpdateProductRequestDto requestDto,
                                              @RequestParam(required = false) Long id, 
                                              @RequestParam(required = false) String name) {
        ProductResponseDto responseDto = productService.updateProduct(requestDto, id, name);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PatchMapping("/product/inventory")
    public ResponseEntity<ProductResponseDto> addToInventory(@RequestBody AddInventoryRequestDto requestDto,
                                              @RequestParam(required = false) Long id, 
                                              @RequestParam(required = false) String name) {
        ProductResponseDto responseDto = productService.addToInventory(requestDto, id, name);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
