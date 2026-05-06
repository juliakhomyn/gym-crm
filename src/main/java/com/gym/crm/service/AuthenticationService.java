package com.gym.crm.service;

import com.gym.crm.dto.common.AuthRequestDTO;
import com.gym.crm.dto.common.AuthResponseDTO;

public interface AuthenticationService {
    AuthResponseDTO authenticate(AuthRequestDTO dto);

    void logout();
}
