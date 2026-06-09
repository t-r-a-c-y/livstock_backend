package com.example.livestock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageReplyRequest(
        @NotNull Long messageId,
        @NotBlank String messageBody
) {
}
