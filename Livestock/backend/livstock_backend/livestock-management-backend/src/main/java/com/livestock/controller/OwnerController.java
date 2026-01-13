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
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<OwnerResponse>> createOwner(@Valid @RequestBody OwnerRequest request) {
        // Service returns Owner (entity)
        Owner createdOwner = ownerService.createOwner(request);

        // Convert entity → DTO for response
        OwnerResponse response = modelMapper.map(createdOwner, OwnerResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OwnerResponse>>> getAllOwners() {
        // Service returns List<Owner> (entities)
        List<Owner> owners = ownerService.getAllOwners();

        // Convert each entity → DTO
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
        Owner updatedOwner = ownerService.updateOwner(id, request);
        OwnerResponse response = modelMapper.map(updatedOwner, OwnerResponse.class);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOwner(@PathVariable UUID id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}