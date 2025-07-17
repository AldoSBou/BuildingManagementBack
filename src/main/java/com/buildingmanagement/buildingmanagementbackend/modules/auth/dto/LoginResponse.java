package com.buildingmanagement.buildingmanagementbackend.modules.auth.dto;

import com.buildingmanagement.buildingmanagementbackend.common.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String name;
    private String email;
    private UserRole role;
    private Long buildingId;
    private long expiresIn;
}

