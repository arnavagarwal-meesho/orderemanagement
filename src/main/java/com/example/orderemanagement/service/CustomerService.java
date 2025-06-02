package com.example.orderemanagement.service;

import com.example.orderemanagement.dto.CustomerLoginRequestDto;
import com.example.orderemanagement.dto.CustomerRequestDto;
import com.example.orderemanagement.dto.CustomerResponseDto;
import com.example.orderemanagement.mapper.CustomerMapper;
import com.example.orderemanagement.model.Customer;
import com.example.orderemanagement.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerResponseDto registerCustomer(CustomerRequestDto requestDto) {
        if(customerRepository.findByEmail(requestDto.getEmail()).isPresent())
            throw new RuntimeException("Email already in use");
        Customer customer = CustomerMapper.toEntity(requestDto);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return CustomerMapper.toResponseDto(customerRepository.save(customer));
    }

    public CustomerResponseDto loginCustomer(CustomerLoginRequestDto requestDto) {
        Customer customer = customerRepository.findByEmail(requestDto.getEmail())
                            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if(passwordEncoder.matches(requestDto.getPassword(), customer.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return CustomerMapper.toResponseDto(customer);
    }

    public CustomerResponseDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Customer not found"));
        return CustomerMapper.toResponseDto(customer);
    }
}
