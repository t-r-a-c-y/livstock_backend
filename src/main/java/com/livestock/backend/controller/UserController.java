package com.livestock.backend.controller;

import com.livestock.backend.dto.ProfileDTO;
import com.livestock.backend.dto.UserRoleDTO;
import com.livestock.backend.service.UserService;
import com.livestock.backend.util.ResponseWrapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<ProfileDTO>>> getAll(Pageable pageable) {
        logger.info("Received request to fetch all users");
        Page<ProfileDTO> users = userService.getAll(pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(users, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ProfileDTO>> getById(@PathVariable UUID id) {
        logger.info("Received request to fetch user with id: {}", id);
        try {
            ProfileDTO user = userService.getById(id);
            return ResponseEntity.ok(new ResponseWrapper<>(user, null));
        } catch (RuntimeException e) {
            logger.error("Error fetching user with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<ProfileDTO>> create(@Valid @RequestBody ProfileDTO userDTO) {
        logger.info("Received request to create user with id: {}", userDTO.getId());
        try {
            ProfileDTO createdUser = userService.create(userDTO);
            return ResponseEntity.ok(new ResponseWrapper<>(createdUser, null));
        } catch (RuntimeException e) {
            logger.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ProfileDTO>> update(@PathVariable UUID id, @Valid @RequestBody ProfileDTO userDTO) {
        logger.info("Received request to update user with id: {}", id);
        try {
            ProfileDTO updatedUser = userService.update(id, userDTO);
            return ResponseEntity.ok(new ResponseWrapper<>(updatedUser, null));
        } catch (RuntimeException e) {
            logger.error("Error updating user with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> delete(@PathVariable UUID id) {
        logger.info("Received request to delete user with id: {}", id);
        try {
            userService.delete(id);
            return ResponseEntity.ok(new ResponseWrapper<>(null, null));
        } catch (RuntimeException e) {
            logger.error("Error deleting user with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<ResponseWrapper<UserRoleDTO>> createRole(@PathVariable UUID id, @Valid @RequestBody UserRoleDTO roleDTO) {
        logger.info("Received request to create role for user with id: {}", id);
        try {
            roleDTO.setUserId(id);
            UserRoleDTO createdRole = userService.createRole(roleDTO);
            return ResponseEntity.ok(new ResponseWrapper<>(createdRole, null));
        } catch (RuntimeException e) {
            logger.error("Error creating role for user with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }
}