package com.livestock.backend.controller;

import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.dto.OwnerDTO;
import com.livestock.backend.dto.OwnerCreateDTO;
import com.livestock.backend.dto.OwnerUpdateDTO;
import com.livestock.backend.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OwnerDTO>>> getAll(Pageable pageable) {
        ApiResponse<Page<OwnerDTO>> response = new ApiResponse<>();
        response.setData(ownerService.getAllOwners(pageable));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OwnerDTO>> getById(@PathVariable UUID id) {
        ApiResponse<OwnerDTO> response = new ApiResponse<>();
        response.setData(ownerService.getOwnerById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OwnerDTO>> create(@Valid @RequestBody OwnerCreateDTO dto) {
        ApiResponse<OwnerDTO> response = new ApiResponse<>();
        response.setData(ownerService.createOwner(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OwnerDTO>> update(@PathVariable UUID id, @Valid @RequestBody OwnerUpdateDTO dto) {
        ApiResponse<OwnerDTO> response = new ApiResponse<>();
        response.setData(ownerService.updateOwner(id, dto));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        ownerService.softDeleteOwner(id);
        return ResponseEntity.noContent().build();
    }
}