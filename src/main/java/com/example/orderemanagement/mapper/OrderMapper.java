package com.example.orderemanagement.mapper;

import com.example.orderemanagement.dto.BuyProductRequestDto;
import com.example.orderemanagement.model.Customer;
import com.example.orderemanagement.model.Order;
import com.example.orderemanagement.model.Product;

public class OrderMapper {
    public static Order toEntity(BuyProductRequestDto requestDto, Customer customer, Product product) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setProduct(product);
        order.setQuantity(requestDto.getQuantity());
        return order;
    }
}
