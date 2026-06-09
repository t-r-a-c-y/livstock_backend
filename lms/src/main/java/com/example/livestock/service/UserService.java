package com.example.livestock.service;

import com.example.livestock.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> findAll(boolean includeInactive, Pageable pageable);
    UserResponse deactivate(Long id);
    UserResponse activate(Long id);
}
