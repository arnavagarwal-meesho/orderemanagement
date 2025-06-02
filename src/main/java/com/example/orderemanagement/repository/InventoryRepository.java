package com.example.orderemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.orderemanagement.model.Inventory;
import com.example.orderemanagement.model.Product;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{
    Optional<Inventory> findByProduct(Product product);
}
