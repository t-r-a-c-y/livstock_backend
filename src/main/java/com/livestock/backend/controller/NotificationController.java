package com.livestock.backend.controller;

import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.dto.NotificationDTO;
import com.livestock.backend.dto.NotificationCreateDTO;
import com.livestock.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationDTO>>> getAll(
            @RequestParam(required = false) Boolean isRead,
            Pageable pageable) {
        ApiResponse<Page<NotificationDTO>> response = new ApiResponse<>();
        response.setData(notificationService.getAllNotifications(isRead, pageable));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDTO>> markAsRead(@PathVariable UUID id) {
        ApiResponse<NotificationDTO> response = new ApiResponse<>();
        response.setData(notificationService.markNotificationAsRead(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationDTO>> create(@Valid @RequestBody NotificationCreateDTO dto) {
        ApiResponse<NotificationDTO> response = new ApiResponse<>();
        response.setData(notificationService.createNotification(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        notificationService.softDeleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}