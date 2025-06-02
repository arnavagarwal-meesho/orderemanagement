package com.example.orderemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.orderemanagement.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long>{
    Optional<Admin> findByEmail(String email);
}
