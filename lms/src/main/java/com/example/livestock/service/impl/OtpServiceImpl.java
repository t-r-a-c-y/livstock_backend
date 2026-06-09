package com.example.livestock.service.impl;

import com.example.livestock.dto.OtpRequest;
import com.example.livestock.dto.OtpVerifyRequest;
import com.example.livestock.entity.OtpToken;
import com.example.livestock.enums.NotificationType;
import com.example.livestock.exception.BadRequestException;
import com.example.livestock.exception.ResourceNotFoundException;
import com.example.livestock.repository.OtpTokenRepository;
import com.example.livestock.repository.UserRepository;
import com.example.livestock.service.EmailService;
import com.example.livestock.service.NotificationService;
import com.example.livestock.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OtpServiceImpl implements OtpService {
    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.otp.expiration-minutes}")
    private long expirationMinutes;

    @Override
    public void sendOtp(OtpRequest request) {
        var user = userRepository.findByEmailAndActiveTrue(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));

        OtpToken token = new OtpToken();
        token.setUser(user);
        token.setCodeHash(passwordEncoder.encode(code));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        otpTokenRepository.save(token);

        emailService.sendOtp(user.getEmail(), user.getFullName(), code);
        notificationService.notify(user, "OTP sent", "A login verification code was sent to your email.", NotificationType.OTP_SENT);
    }

    @Override
    public void verify(OtpVerifyRequest request) {
        OtpToken token = otpTokenRepository.findTopByUserEmailAndActiveTrueAndUsedFalseOrderByCreatedAtDesc(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            token.setActive(false);
            throw new BadRequestException("Invalid or expired OTP");
        }
        if (!passwordEncoder.matches(request.code(), token.getCodeHash())) {
            throw new BadRequestException("Invalid or expired OTP");
        }
        token.setUsed(true);
        token.setActive(false);
    }
}
