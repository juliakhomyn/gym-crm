package com.gym.crm.util;

import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.model.User;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

@Component
public class UserCredentialGenerator {
    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String SEPARATOR = ".";
    private static final int PASSWORD_LENGTH = 10;
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvxyz"
            + "0123456789";

    @Setter(onMethod_={@Autowired})
    private TraineeDAO traineeDAO;

    @Setter(onMethod_={@Autowired})
    private TrainerDAO trainerDAO;

    public String generateUsername(String firstName, String lastName) {
        Validator.validateNotBlank(firstName, FIRST_NAME);
        Validator.validateNotBlank(lastName, LAST_NAME);

        String username = toCamelCase(firstName) + SEPARATOR + toCamelCase(lastName);
        long serialNumber = getSerialNumber(username);

        return username + (serialNumber == 0 ? "" : serialNumber);
    }

    public String generatePassword() {
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = (int) (Math.random() * CHARS.length());
            password.append(CHARS.charAt(index));
        }

        return password.toString();
    }

    private String toCamelCase(String username) {
        Validator.validateNotBlank(username, "Username");

        return Character.toUpperCase(username.charAt(0)) + username.substring(1).toLowerCase();
    }

    private long getSerialNumber(String usernameToFind) {
        return getAllUsernames()
                .filter(Objects::nonNull)
                .filter(username -> !username.isBlank())
                .filter(username -> username.matches(usernameToFind + "\\d*"))
                .count();
    }

    private Stream<String> getAllUsernames() {
        return Stream.concat(
                    traineeDAO.findAll().stream().map(User::getUsername),
                    trainerDAO.findAll().stream().map(User::getUsername)
                )
                .filter(Objects::nonNull);
    }
}
