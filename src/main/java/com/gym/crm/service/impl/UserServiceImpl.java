package com.gym.crm.service.impl;

import com.gym.crm.dao.UserDAO;
import com.gym.crm.model.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.UserService;
import com.gym.crm.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_BY_ID = "User not found by id: %s";
    private static final String USER_NOT_FOUND_BY_USERNAME = "User not found by username: %s";

    private final PasswordEncoder passwordEncoder;
    private final UserDAO dao;

    @Override
    public User getByUsername(String username) {
        return dao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_USERNAME, username)));
    }

    @Override
    public User getById(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_ID, id)));
    }

    @Override
    public List<User> getAll() {
        return dao.findAll();
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        Validator.validateNotBlank(username, "Username");
        Validator.validateNotBlank(oldPassword, "Old Password");
        Validator.validateNotBlank(newPassword, "New Password");
        log.info("Changing password for user: username={}", username);

        User user = dao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_USERNAME, username)));
        User userWithNewPassword = user.toBuilder()
                .password(passwordEncoder.encode(newPassword))
                .build();

        dao.update(userWithNewPassword);
        log.info("Changed password for user: username={}", username);
    }

    @Override
    public void toggleActive(String username) {
        Validator.validateNotBlank(username, "Username");
        log.info("Changing active status for user: username={}", username);

        User user = dao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND_BY_USERNAME, username)));

        boolean currentStatus = user.getIsActive();
        User userWithChangedStatus = user.toBuilder()
                .isActive(!currentStatus)
                .build();

        dao.update(userWithChangedStatus);
        log.info("User {}: username={}", currentStatus ? "deactivated" : "activated", username);
    }
}
