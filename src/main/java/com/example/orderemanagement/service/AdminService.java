package com.example.orderemanagement.service;

import com.example.orderemanagement.model.Admin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.orderemanagement.dto.AdminLoginRequestDto;
import com.example.orderemanagement.dto.AdminRequestDto;
import com.example.orderemanagement.dto.AdminResponseDto;
import com.example.orderemanagement.mapper.AdminMapper;
import com.example.orderemanagement.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.registration-secret}")
    private String expectedSecret;

    public AdminResponseDto register(AdminRequestDto requestDto) {
        if(adminRepository.findByEmail(requestDto.getEmail()).isPresent())
            throw new RuntimeException("Email already in use");
        if (!requestDto.getAdminSecret().equals(expectedSecret)) 
                throw new RuntimeException("Invalid admin secret key");
        Admin admin = AdminMapper.toEntity(requestDto);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return AdminMapper.toResponseDto(adminRepository.save(admin));
    }

    public AdminResponseDto login(AdminLoginRequestDto requestDto) {
        Admin admin = adminRepository.findByEmail(requestDto.getEmail())
                            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if(!passwordEncoder.matches(requestDto.getPassword(), admin.getPassword()))
            throw new RuntimeException("Invalid email or password");

        return AdminMapper.toResponseDto(admin);
    }
}
