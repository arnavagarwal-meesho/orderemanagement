package com.example.orderemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.orderemanagement.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
    Optional<Product> findByName(String name);
} 
