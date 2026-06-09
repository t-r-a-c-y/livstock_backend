package com.example.livestock.config;

import com.example.livestock.entity.User;
import com.example.livestock.enums.AccountStatus;
import com.example.livestock.enums.Role;
import com.example.livestock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedDefaultAdmin(
            @Value("${DEFAULT_ADMIN_EMAIL:admin@livestock.local}") String email,
            @Value("${DEFAULT_ADMIN_PASSWORD:Admin@12345}") String password) {
        return args -> {
            if (!userRepository.existsByEmail(email)) {
                User admin = new User();
                admin.setFullName("System Administrator");
                admin.setEmail(email);
                admin.setPassword(passwordEncoder.encode(password));
                admin.setRole(Role.ADMIN);
                admin.setAccountStatus(AccountStatus.ACTIVE);
                userRepository.save(admin);
            }
        };
    }
}
