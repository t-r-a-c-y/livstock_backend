package com.example.livestock.repository;

import com.example.livestock.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByUserEmailAndActiveTrueAndUsedFalseOrderByCreatedAtDesc(String email);
}
