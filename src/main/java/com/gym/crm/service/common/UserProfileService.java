package com.gym.crm.service.common;

import com.gym.crm.service.helper.PasswordGenerator;
import com.gym.crm.service.helper.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final UsernameGenerator usernameGenerator;

    public String generateUsername(String firstName, String lastName) {
        return usernameGenerator.generateUsername(firstName, lastName);
    }

    public String generatePassword() {
        return passwordGenerator.generatePassword();
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
