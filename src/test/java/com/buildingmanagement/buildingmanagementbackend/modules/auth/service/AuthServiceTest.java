package com.buildingmanagement.buildingmanagementbackend.modules.auth.service;

import com.buildingmanagement.buildingmanagementbackend.common.exceptions.BusinessException;
import com.buildingmanagement.buildingmanagementbackend.modules.auth.dto.SignupRequest;
import com.buildingmanagement.buildingmanagementbackend.modules.user.entity.User;
import com.buildingmanagement.buildingmanagementbackend.modules.user.repository.UserRepository;
import com.buildingmanagement.buildingmanagementbackend.security.JwtTokenProvider;
import com.buildingmanagement.buildingmanagementbackend.common.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded_password")
                .name("Test User")
                .role(UserRole.OWNER)
                .isActive(true)
                .build();

        signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setName("Test User");
        signupRequest.setRole(UserRole.OWNER);
    }

    @Test
    void signup_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        String result = authService.signup(signupRequest);

        // Then
        assertEquals("User registered successfully", result);
        verify(userRepository).existsByEmail(signupRequest.getEmail());
        verify(passwordEncoder).encode(signupRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.signup(signupRequest));

        assertEquals("Email is already registered", exception.getMessage());
        verify(userRepository).existsByEmail(signupRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
}