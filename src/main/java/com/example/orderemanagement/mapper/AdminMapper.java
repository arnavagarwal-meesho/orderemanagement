package com.example.orderemanagement.mapper;

import com.example.orderemanagement.dto.AdminRequestDto;
import com.example.orderemanagement.dto.AdminResponseDto;
import com.example.orderemanagement.model.Admin;

public class AdminMapper {
    public static Admin toEntity(AdminRequestDto requestDto) {
        Admin admin = new Admin();
        admin.setEmail(requestDto.getEmail());
        admin.setName(requestDto.getName());
        admin.setPassword(requestDto.getPassword());
        return admin;
    }
    
    public static AdminResponseDto toResponseDto(Admin admin) {
        AdminResponseDto adminResponseDto = new AdminResponseDto();
        adminResponseDto.setId(admin.getId());
        adminResponseDto.setEmail(admin.getEmail());
        adminResponseDto.setName(admin.getName());
        return adminResponseDto;
    }
}
