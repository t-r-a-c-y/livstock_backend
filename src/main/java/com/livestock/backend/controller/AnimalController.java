package com.livestock.backend.controller;

import com.livestock.backend.dto.AnimalDTO;
import com.livestock.backend.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @GetMapping
    public ResponseEntity<?> getAnimals(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID ownerId,
            Pageable pageable) {
        try {
            Page<AnimalDTO> animals = animalService.getAll(type, status, ownerId, pageable);
            return ResponseEntity.ok().body(new ResponseWrapper<>(animals, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ErrorResponse(e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAnimal(@PathVariable UUID id) {
        try {
            AnimalDTO animal = animalService.getById(id);
            return ResponseEntity.ok().body(new ResponseWrapper<>(animal, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ErrorResponse(e.getMessage())));
        }
    }

    @PostMapping
    public ResponseEntity<?> createAnimal(@RequestBody AnimalDTO dto) {
        try {
            AnimalDTO created = animalService.create(dto);
            return ResponseEntity.status(201).body(new ResponseWrapper<>(created, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ErrorResponse(e.getMessage())));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnimal(@PathVariable UUID id, @RequestBody AnimalDTO dto) {
        try {
            AnimalDTO updated = animalService.update(id, dto);
            return ResponseEntity.ok().body(new ResponseWrapper<>(updated, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ErrorResponse(e.getMessage())));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnimal(@PathVariable UUID id) {
        try {
            animalService.delete(id);
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