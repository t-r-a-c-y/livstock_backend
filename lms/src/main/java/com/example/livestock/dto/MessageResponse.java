package com.example.livestock.dto;

import com.example.livestock.enums.MessageStatus;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long senderId,
        String senderName,
        Long receiverId,
        String receiverName,
        Long animalId,
        String animalTagNumber,
        String subject,
        String messageBody,
        MessageStatus messageStatus,
        LocalDateTime createdAt
) {
}
