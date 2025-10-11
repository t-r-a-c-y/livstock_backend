package com.livestock.backend.controller;

import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.dto.UserDTO;
import com.livestock.backend.dto.UserCreateDTO;
import com.livestock.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAll() {
        ApiResponse<List<UserDTO>> response = new ApiResponse<>();
        response.setData(userService.getAllUsers());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getById(@PathVariable UUID id) {
        ApiResponse<UserDTO> response = new ApiResponse<>();
        response.setData(userService.getUserById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> create(@Valid @RequestBody UserCreateDTO dto) {
        ApiResponse<UserDTO> response = new ApiResponse<>();
        response.setData(userService.createUser(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> update(@PathVariable UUID id, @Valid @RequestBody UserDTO dto) {
        ApiResponse<UserDTO> response = new ApiResponse<>();
        response.setData(userService.updateUser(id, dto));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}