package com.livestock.backend.controller;

import com.livestock.backend.dto.NotificationDTO;
import com.livestock.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getNotifications(@RequestParam(required = false) Boolean isRead, Pageable pageable) {
        try {
            Page<NotificationDTO> notifications = notificationService.getAll(isRead, pageable);
            return ResponseEntity.ok().body(new ResponseWrapper<>(notifications, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ErrorResponse(e.getMessage())));
        }
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable UUID id) {
        try {
            notificationService.markAsRead(id);
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