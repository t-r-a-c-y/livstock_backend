package com.livestock.backend.controller;

import com.livestock.backend.dto.OwnerDTO;
import com.livestock.backend.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @GetMapping
    public ResponseEntity<?> getOwners(Pageable pageable) {
        try {
            Page<OwnerDTO> owners = ownerService.getAll(pageable);
            return ResponseEntity.ok().body(new ResponseWrapper<>(owners, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ErrorResponse(e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOwner(@PathVariable UUID id) {
        try {
            OwnerDTO owner = ownerService.getById(id);
            return ResponseEntity.ok().body(new ResponseWrapper<>(owner, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ErrorResponse(e.getMessage())));
        }
    }

    @PostMapping
    public ResponseEntity<?> createOwner(@RequestBody OwnerDTO dto) {
        try {
            OwnerDTO created = ownerService.create(dto);
            return ResponseEntity.status(201).body(new ResponseWrapper<>(created, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ErrorResponse(e.getMessage())));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOwner(@PathVariable UUID id, @RequestBody OwnerDTO dto) {
        try {
            OwnerDTO updated = ownerService.update(id, dto);
            return ResponseEntity.ok().body(new ResponseWrapper<>(updated, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ErrorResponse(e.getMessage())));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOwner(@PathVariable UUID id) {
        try {
            ownerService.delete(id);
            return ResponseEntity.ok().body(new ResponseWrapper<>(null, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ErrorResponse(e.getMessage())));
        }
    }

    private static class ResponseWrapper<T> {
        private final T data;
        private final ErrorResponse error;

        public ResponseWrapper(T data, ErrorResponse error) {
            this.data = data;
            this.error = error;
        }

        public T getData() {
            return data;
        }

        public ErrorResponse getError() {
            return error;
        }
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}