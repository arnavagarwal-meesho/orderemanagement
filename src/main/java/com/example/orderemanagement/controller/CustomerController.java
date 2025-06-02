package com.example.orderemanagement.controller;

import com.example.orderemanagement.dto.BuyProductRequestDto;
import com.example.orderemanagement.dto.CustomerLoginRequestDto;
import com.example.orderemanagement.dto.CustomerRequestDto;
import com.example.orderemanagement.dto.CustomerResponseDto;
import com.example.orderemanagement.dto.ProductResponseDto;
import com.example.orderemanagement.service.CustomerService;
import com.example.orderemanagement.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final ProductService productService;

    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDto> registerCustomer(@Valid @RequestBody CustomerRequestDto requestDto) {
        CustomerResponseDto savedCustomer = customerService.registerCustomer(requestDto);
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerResponseDto> loginCustomer(@Valid @RequestBody CustomerLoginRequestDto requestDto) {
        CustomerResponseDto loginResponse = customerService.loginCustomer(requestDto);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable Long id) {
        CustomerResponseDto responseDto = customerService.getCustomerById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/products/buy")
    public ResponseEntity<String> buyProduct(@RequestBody BuyProductRequestDto requestDto,
                                              @RequestParam(required = false) Long id, 
                                              @RequestParam(required = false) String name) {
        productService.buyProduct(requestDto, id, name);
        return new ResponseEntity<>("Product bought successfully", HttpStatus.OK);
    }
}
