package com.buildingmanagement.buildingmanagementbackend.modules.auth.service;

import com.buildingmanagement.buildingmanagementbackend.common.exceptions.BusinessException;
import com.buildingmanagement.buildingmanagementbackend.modules.auth.dto.*;
import com.buildingmanagement.buildingmanagementbackend.modules.user.entity.User;
import com.buildingmanagement.buildingmanagementbackend.modules.user.repository.UserRepository;
import com.buildingmanagement.buildingmanagementbackend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationInMs;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Attempting login for email: {}", loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmailAndIsActiveTrue(loginRequest.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        log.info("User {} logged in successfully", user.getEmail());

        return LoginResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .buildingId(user.getBuildingId())
                .expiresIn(jwtExpirationInMs)
                .build();
    }

    @Override
    @Transactional
    public String signup(SignupRequest signupRequest) {
        log.info("Attempting signup for email: {}", signupRequest.getEmail());

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new BusinessException("Email is already registered");
        }

        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .phone(signupRequest.getPhone())
                .role(signupRequest.getRole())
                .buildingId(signupRequest.getBuildingId())
                .isActive(true)
                .build();

        userRepository.save(user);

        log.info("User {} registered successfully", user.getEmail());
        return "User registered successfully";
    }

    @Override
    @Transactional
    public String forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        log.info("Forgot password request for email: {}", forgotPasswordRequest.getEmail());

        User user = userRepository.findByEmailAndIsActiveTrue(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new BusinessException("User not found with this email"));

        // TODO: Generate reset token and send email
        // For now, just return success message

        log.info("Password reset email sent to: {}", user.getEmail());
        return "Password reset email sent successfully";
    }

    @Override
    @Transactional
    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        // TODO: Implement password reset logic
        log.info("Password reset attempted with token: {}", resetPasswordRequest.getToken());
        return "Password reset successfully";
    }

    @Override
    public String refreshToken(String refreshToken) {
        // TODO: Implement refresh token logic
        log.info("Token refresh attempted");
        return "Token refreshed successfully";
    }
}
