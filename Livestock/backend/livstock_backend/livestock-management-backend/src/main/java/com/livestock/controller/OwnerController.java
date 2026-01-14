package com.livestock.controller;

import com.livestock.dto.request.OwnerRequest;
import com.livestock.dto.response.OwnerResponse;
import com.livestock.dto.response.ApiResponse;
import com.livestock.entity.Owner;
import com.livestock.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor  // ← FIXED TYPO: one "Args"
public class OwnerController {

    private final OwnerService ownerService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<OwnerResponse>> createOwner(@Valid @RequestBody OwnerRequest request) {
        Owner created = ownerService.createOwner(request);
        OwnerResponse response = modelMapper.map(created, OwnerResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OwnerResponse>>> getAllOwners() {
        List<Owner> owners = ownerService.getAllOwners();
        List<OwnerResponse> responses = owners.stream()
                .map(owner -> modelMapper.map(owner, OwnerResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OwnerResponse>> getOwnerById(@PathVariable UUID id) {
        Owner owner = ownerService.getOwnerById(id);
        OwnerResponse response = modelMapper.map(owner, OwnerResponse.class);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OwnerResponse>> updateOwner(
            @PathVariable UUID id, @Valid @RequestBody OwnerRequest request) {
        Owner updated = ownerService.updateOwner(id, request);
        OwnerResponse response = modelMapper.map(updated, OwnerResponse.class);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOwner(@PathVariable UUID id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}