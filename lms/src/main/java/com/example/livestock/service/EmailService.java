package com.example.livestock.service;

public interface EmailService {
    void sendOtp(String to, String fullName, String code);
}
