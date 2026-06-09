package com.example.livestock.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpVerifyRequest(
        @Email @NotBlank String email,
        @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits") String code
) {
}
