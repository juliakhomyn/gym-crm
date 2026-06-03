package com.gym.crm.service.common;

import com.gym.crm.auth.SessionContext;
import com.gym.crm.dto.common.AuthRequestDTO;
import com.gym.crm.dto.common.AuthResponseDTO;
import com.gym.crm.model.User;
import com.gym.crm.exception.BadCredentialsException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.repository.UserRepository;
import com.gym.crm.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final UserProfileService service;
    private final JwtService jwtService;
    private final SessionContext sessionContext;

    @Transactional(readOnly = true)
    public AuthResponseDTO authenticate(@Valid AuthRequestDTO dto) {
        log.info("Authentication attempt for user: {}", dto.getUsername());

        User user = repository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + dto.getUsername()));
        if (!service.checkPassword(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials for user: " + dto.getUsername());
        }

        log.info("Authentication successful for user: {}", dto.getUsername());
        sessionContext.setAuthenticatedUser(user);

        return AuthResponseDTO.builder()
                .username(user.getUsername())
                .token(jwtService.generateToken(user.getUsername()))
                .build();
    }

    public void logout() {
        log.info("Logging out user: {}", sessionContext.getAuthenticatedUser().getUsername());
        sessionContext.clear();
    }
}
