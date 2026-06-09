package com.example.livestock.service.impl;

import com.example.livestock.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendOtp(String to, String fullName, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your Livestock Management OTP");
        message.setText("""
                Hello %s,

                Your OTP code is %s.

                This code expires soon. If you did not request it, please ignore this email.
                """.formatted(fullName, code));
        mailSender.send(message);
    }
}
