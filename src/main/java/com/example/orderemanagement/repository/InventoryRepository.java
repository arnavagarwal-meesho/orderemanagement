package com.example.orderemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.orderemanagement.model.Inventory;
import com.example.orderemanagement.model.Product;

import jakarta.persistence.LockModeType;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{
    Optional<Inventory> findByProduct(Product product);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.product = :product")
    Optional<Inventory> findByProductWithLock(@Param("product") Product product);
}
