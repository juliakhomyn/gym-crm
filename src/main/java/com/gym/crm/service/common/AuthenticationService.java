package com.gym.crm.service.common;

import com.gym.crm.auth.SessionContext;
import com.gym.crm.dao.UserDAO;
import com.gym.crm.dto.common.AuthRequestDTO;
import com.gym.crm.dto.common.AuthResponseDTO;
import com.gym.crm.model.User;
import com.gym.crm.exception.BadCredentialsException;
import com.gym.crm.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDAO dao;
    private final UserProfileService service;
    private final UserInputValidator validator;
    private final SessionContext sessionContext;

    public AuthResponseDTO authenticate(AuthRequestDTO dto) {
        validator.validate(dto, "Authentication request");
        log.info("Authentication attempt for user: {}", dto.getUsername());

        User user = dao.findByUsername(dto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + dto.getUsername()));
        if (!service.checkPassword(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials for user: " + dto.getUsername());
        }

        log.info("Authentication successful for user: {}", dto.getUsername());
        sessionContext.setAuthenticatedUser(user);

        return AuthResponseDTO.builder()
                .username(user.getUsername())
                .message("Authentication successful!")
                .build();
    }

    public void logout() {
        log.info("Logging out user: {}", sessionContext.getAuthenticatedUser().getUsername());
        sessionContext.clear();
    }
}
