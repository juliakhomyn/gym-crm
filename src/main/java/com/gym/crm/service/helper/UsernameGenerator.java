package com.gym.crm.service.helper;

import com.gym.crm.dao.UserDAO;
import com.gym.crm.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsernameGenerator {
    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String SEPARATOR = ".";

    private final UserDAO dao;

    public String generateUsername(String firstName, String lastName) {
        Validator.validateNotBlank(firstName, FIRST_NAME);
        Validator.validateNotBlank(lastName, LAST_NAME);

        String baseUsername = (firstName + SEPARATOR + lastName);

        if (!dao.existsByUsername(baseUsername)) {
            return baseUsername;
        }

        long serialNumber = 1;
        while (dao.existsByUsername(baseUsername + serialNumber)) {
            serialNumber++;
        }

        log.warn("Username {} already exists, serial number {} will be appended", baseUsername, serialNumber);
        return baseUsername + serialNumber;
    }
}
