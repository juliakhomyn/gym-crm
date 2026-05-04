package com.gym.crm.service;

import com.gym.crm.model.User;

import java.util.List;

public interface UserService {
    User getByUsername(String username);

    User getById(Long id);

    List<User> getAll();

    void changePassword(String username, String oldPassword, String newPassword);

    void toggleActive(String username);
}
