package com.buildingmanagement.buildingmanagementbackend.modules.auth.controller;

import com.buildingmanagement.buildingmanagementbackend.modules.auth.dto.*;
import com.buildingmanagement.buildingmanagementbackend.modules.auth.service.AuthService;
import com.buildingmanagement.buildingmanagementbackend.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        String message = authService.signup(signupRequest);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        String message = authService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        String message = authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestParam String refreshToken) {
        String message = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}