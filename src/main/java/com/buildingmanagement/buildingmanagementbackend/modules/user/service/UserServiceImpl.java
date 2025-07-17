package com.buildingmanagement.buildingmanagementbackend.modules.user.service;

import com.buildingmanagement.buildingmanagementbackend.modules.user.entity.User;
import com.buildingmanagement.buildingmanagementbackend.modules.user.repository.UserRepository;
import com.buildingmanagement.buildingmanagementbackend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrUserId) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", emailOrUserId);

        User user;

        // Check if it's a numeric ID (for JWT token validation) or email (for login)
        if (emailOrUserId.matches("\\d+")) {
            Long userId = Long.parseLong(emailOrUserId);
            log.debug("Loading user by ID: {}", userId);
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        } else {
            log.debug("Loading user by email: {}", emailOrUserId);
            user = userRepository.findByEmailAndIsActiveTrue(emailOrUserId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emailOrUserId));
        }

        log.debug("User found: {} with role: {}", user.getEmail(), user.getRole());
        return UserPrincipal.create(user);
    }
}