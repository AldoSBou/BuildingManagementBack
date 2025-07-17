package com.buildingmanagement.buildingmanagementbackend.modules.auth.controller;

import com.buildingmanagement.buildingmanagementbackend.security.UserPrincipal;
import com.buildingmanagement.buildingmanagementbackend.shared.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> publicEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("This is a public endpoint"));
    }

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<Map<String, Object>>> protectedEndpoint(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Map<String, Object> data = new HashMap<>();
        data.put("message", "This is a protected endpoint");
        data.put("userId", currentUser.getId());
        data.put("email", currentUser.getEmail());
        data.put("name", currentUser.getName());
        data.put("authorities", currentUser.getAuthorities());
        data.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<String>> adminEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("This is an admin endpoint"));
    }
}