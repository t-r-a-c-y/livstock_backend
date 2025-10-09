package com.livestock.backend.controller;

import com.livestock.backend.dto.OwnerDTO;
import com.livestock.backend.dto.OwnerWithAnimalCountDTO;
import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {
    @Autowired
    private OwnerService ownerService;

    @GetMapping
    public ApiResponse<Page<OwnerWithAnimalCountDTO>> list(
            @RequestParam(required = false) String name,
            Pageable pageable) {
        return new ApiResponse<>(ownerService.getAll(name, pageable), null);
    }

    @GetMapping("/{id}")
    public ApiResponse<OwnerDTO> get(@PathVariable UUID id) {
        return new ApiResponse<>(ownerService.getById(id), null);
    }

    @PostMapping
    public ApiResponse<OwnerDTO> create(@Valid @RequestBody OwnerDTO dto) {
        return new ApiResponse<>(ownerService.create(dto), null);
    }

    @PutMapping("/{id}")
    public ApiResponse<OwnerDTO> update(@PathVariable UUID id, @Valid @RequestBody OwnerDTO dto) {
        return new ApiResponse<>(ownerService.update(id, dto), null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        ownerService.softDelete(id);
        return new ApiResponse<>(null, null);
    }
}