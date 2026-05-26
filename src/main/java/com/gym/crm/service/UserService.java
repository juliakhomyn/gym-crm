package com.gym.crm.service;

import com.gym.crm.dto.common.PasswordChangeRequest;
import com.gym.crm.dto.common.ToggleActiveRequestDTO;
import com.gym.crm.dto.validation.ValidId;
import com.gym.crm.dto.validation.ValidUsername;
import com.gym.crm.model.User;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface UserService {
    User getByUsername(@ValidUsername String username);

    User getById(@ValidId Long id);

    List<User> getAll();

    void changePassword(@Valid PasswordChangeRequest request);

    void toggleActive(@Valid ToggleActiveRequestDTO request);
}
