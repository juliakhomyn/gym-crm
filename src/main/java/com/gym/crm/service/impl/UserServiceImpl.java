package com.gym.crm.service.impl;

import com.gym.crm.dao.UserDAO;
import com.gym.crm.dto.PasswordChangeRequest;
import com.gym.crm.dto.ToggleActiveRequestDTO;
import com.gym.crm.model.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.UserService;
import com.gym.crm.service.common.ValidationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_BY_ID = "User not found by id: %s";
    private static final String USER_NOT_FOUND_BY_USERNAME = "User not found by username: %s";

    private final PasswordEncoder passwordEncoder;
    private final UserDAO dao;
    private final ValidationService validationService;

    @Override
    public User getByUsername(String username) {
        validationService.validate(username);

        return dao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_USERNAME, username)));
    }

    @Override
    public User getById(Long id) {
        validationService.validate(id);

        return dao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_ID, id)));
    }

    @Override
    public List<User> getAll() {
        return dao.findAll();
    }

    @Override
    public void changePassword(@Valid PasswordChangeRequest request) {
        validationService.validate(request);
        log.info("Changing password for user: username={}", request.getUsername());

        User user = dao.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_USERNAME, request.getUsername())));
        User userWithNewPassword = user.toBuilder()
                .password(passwordEncoder.encode(request.getNewPassword()))
                .build();

        dao.update(userWithNewPassword);
        log.info("Changed password for user: username={}", request.getUsername());
    }

    @Override
    public void toggleActive(@Valid ToggleActiveRequestDTO request) {
        validationService.validate(request);
        log.info("Changing active status for user: username={}", request.getUsername());

        User user = dao.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_USERNAME, request.getUsername())));

        boolean currentStatus = user.getIsActive();
        User userWithChangedStatus = user.toBuilder()
                .isActive(!currentStatus)
                .build();

        dao.update(userWithChangedStatus);
        log.info("User {}: username={}", currentStatus ? "deactivated" : "activated", request.getUsername());
    }
}
