package com.buildingmanagement.buildingmanagementbackend.modules.auth.service;

import com.buildingmanagement.buildingmanagementbackend.modules.auth.dto.*;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    String signup(SignupRequest signupRequest);
    String forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    String resetPassword(ResetPasswordRequest resetPasswordRequest);
    String refreshToken(String refreshToken);
}
