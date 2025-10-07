package com.livestock.backend.controller;



import com.livestock.backend.dto.request.OwnerRequest;
import com.livestock.backend.dto.response.OwnerResponse;
import com.livestock.backend.service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public ResponseEntity<Page<OwnerResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ownerService.getAllOwners(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody OwnerRequest request) {
        OwnerResponse response = ownerService.createOwner(request);
        return ResponseEntity.ok(Map.of("id", response.getId(), "message", "Owner created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id, @Valid @RequestBody OwnerRequest request) {
        OwnerResponse response = ownerService.updateOwner(id, request);
        return ResponseEntity.ok(Map.of("id", response.getId(), "message", "Owner updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.ok(Map.of("message", "Owner deleted successfully"));
    }
}
