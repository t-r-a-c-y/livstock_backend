package com.example.livestock.service;

import com.example.livestock.dto.OtpRequest;
import com.example.livestock.dto.OtpVerifyRequest;

public interface OtpService {
    void sendOtp(OtpRequest request);
    void verify(OtpVerifyRequest request);
}
