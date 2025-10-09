package com.livestock.backend.controller;

import com.livestock.backend.dto.UserCreateDTO;
import com.livestock.backend.dto.UserDTO;
import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse<Page<UserDTO>> list(Pageable pageable) {
        return new ApiResponse<>(userService.getAll(pageable), null);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> get(@PathVariable UUID id) {
        return new ApiResponse<>(userService.getById(id), null);
    }

    @PostMapping
    public ApiResponse<UserDTO> create(@Valid @RequestBody UserCreateDTO dto) {
        return new ApiResponse<>(userService.create(dto), null);
    }

    @PutMapping("/{id}")
    public ApiResponse<UserDTO> update(@PathVariable UUID id, @Valid @RequestBody UserDTO dto) {
        return new ApiResponse<>(userService.update(id, dto), null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        userService.softDelete(id);
        return new ApiResponse<>(null, null);
    }
}