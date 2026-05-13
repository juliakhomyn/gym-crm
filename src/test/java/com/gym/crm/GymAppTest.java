package com.gym.crm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GymAppTest {

    @Test
    void main_shouldStartSpringContextWithoutException() {
        assertDoesNotThrow(() -> GymApp.main(new String[]{}));
    }
}
