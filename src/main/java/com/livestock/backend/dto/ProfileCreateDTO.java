package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
public class ProfileCreateDTO {
    private String name;
    private String phone;
    private String avatar;
}
