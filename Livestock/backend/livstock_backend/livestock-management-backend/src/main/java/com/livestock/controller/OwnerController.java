// src/main/java/com/livestock/controller/OwnerController.java
package com.livestock.controller;

import com.livestock.dto.request.OwnerRequest;
import com.livestock.dto.response.OwnerResponse;
import com.livestock.dto.response.ApiResponse;
import com.livestock.service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    // Explicit constructor — fixes "not initialized" error
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OwnerResponse>>> getAllOwners() {
        List<OwnerResponse> owners = ownerService.getAllOwners();
        return ResponseEntity.ok(ApiResponse.success(owners));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OwnerResponse>> getOwner(@PathVariable UUID id) {
        OwnerResponse owner = ownerService.getOwnerById(id);
        return ResponseEntity.ok(ApiResponse.success(owner));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OwnerResponse>> createOwner( @RequestBody OwnerRequest request) {
        OwnerResponse owner = ownerService.createOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(owner));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OwnerResponse>> updateOwner(
            @PathVariable UUID id, @Valid @RequestBody OwnerRequest request) {
        OwnerResponse owner = ownerService.updateOwner(id, request);
        return ResponseEntity.ok(ApiResponse.success(owner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOwner(@PathVariable UUID id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}