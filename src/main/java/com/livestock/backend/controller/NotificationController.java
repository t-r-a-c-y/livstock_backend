package com.livestock.backend.controller;


import com.livestock.backend.dto.NotificationDTO;
import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ApiResponse<Page<NotificationDTO>> list(Pageable pageable) {
        return new ApiResponse<>(notificationService.getAll(pageable), null);
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return new ApiResponse<>(null, null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        notificationService.softDelete(id);
        return new ApiResponse<>(null, null);
    }
}