package com.gym.crm.service;

import com.gym.crm.dto.PasswordChangeRequest;
import com.gym.crm.dto.ToggleActiveRequestDTO;
import com.gym.crm.model.User;

import java.util.List;

public interface UserService {
    User getByUsername(String username);

    User getById(Long id);

    List<User> getAll();

    void changePassword(PasswordChangeRequest request);

    void toggleActive(ToggleActiveRequestDTO request);
}
