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
        // Service creates and returns the entity
        Owner createdOwner = ownerService.createOwner(request);

        // Map entity → DTO for response
        OwnerResponse responseDto = modelMapper.map(createdOwner, OwnerResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OwnerResponse>>> getAllOwners() {
        // Service returns list of entities
        List<Owner> owners = ownerService.getAllOwners();

        // Map each entity to DTO
        List<OwnerResponse> responseDtos = owners.stream()
                .map(owner -> modelMapper.map(owner, OwnerResponse.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responseDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OwnerResponse>> getOwnerById(@PathVariable UUID id) {
        Owner owner = ownerService.getOwnerById(id);
        OwnerResponse responseDto = modelMapper.map(owner, OwnerResponse.class);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OwnerResponse>> updateOwner(
            @PathVariable UUID id, @Valid @RequestBody OwnerRequest request) {
        Owner updatedOwner = ownerService.updateOwner(id, request);
        OwnerResponse responseDto = modelMapper.map(updatedOwner, OwnerResponse.class);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOwner(@PathVariable UUID id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}