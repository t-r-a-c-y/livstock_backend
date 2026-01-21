package com.livestock.controller;

import com.livestock.dto.OwnerDto;
import com.livestock.dto.ApiResponse;
import com.livestock.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<OwnerDto>>> getAllOwners() {
        return ResponseEntity.ok(ApiResponse.success(ownerService.getAllOwners()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','VIEWER')")
    public ResponseEntity<ApiResponse<OwnerDto>> getOwnerById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(ownerService.getOwnerById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<OwnerDto>> createOwner(@Valid @RequestBody OwnerDto dto) {
        OwnerDto created = ownerService.createOwner(dto);
        return ResponseEntity.ok(ApiResponse.success(created, "Owner created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<OwnerDto>> updateOwner(
            @PathVariable UUID id,
            @Valid @RequestBody OwnerDto dto) {

        OwnerDto updated = ownerService.updateOwner(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Owner updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOwner(@PathVariable UUID id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Owner deleted"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<OwnerDto>>> searchOwners(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.success(ownerService.searchOwnersByName(name)));
    }
}