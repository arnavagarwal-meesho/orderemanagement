package com.example.orderemanagement.mapper;

import com.example.orderemanagement.dto.CustomerRequestDto;
import com.example.orderemanagement.dto.CustomerResponseDto;
import com.example.orderemanagement.model.Customer;

public class CustomerMapper {
    public static Customer toEntity(CustomerRequestDto requestDto) {
        Customer customer = new Customer();
        customer.setEmail(requestDto.getEmail());
        customer.setName(requestDto.getName());
        customer.setAddress(requestDto.getAddress());
        customer.setPassword(requestDto.getPassword());
        return customer;
    }

    public static CustomerResponseDto toResponseDto(Customer customer) {
        CustomerResponseDto customerResponseDto = new CustomerResponseDto();
        customerResponseDto.setId(customer.getId());
        customerResponseDto.setEmail(customer.getEmail());
        customerResponseDto.setName(customer.getName());
        customerResponseDto.setAddress(customer.getAddress());
        return customerResponseDto;
    }
}
