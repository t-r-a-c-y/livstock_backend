package com.livestock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ← Replace with your actual frontend origins
        // For development: allow localhost React/Vue/etc.
        // For production: add your real domain(s)
        List<String> allowedOrigins = Arrays.asList(
                "http://localhost:3000",           // React dev server
                "http://localhost:5173",           // Vite dev server
                "http://localhost:4200",           // Angular dev server
                "https://your-frontend-domain.com" // ← add production domain here
        );

        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Origin"
        ));
        config.setExposedHeaders(Arrays.asList("Authorization")); // important for JWT token
        config.setAllowCredentials(true);   // ← allow cookies/credentials (if needed)
        config.setMaxAge(3600L);           // cache preflight for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // apply to all paths

        return new CorsFilter(source);
    }
}