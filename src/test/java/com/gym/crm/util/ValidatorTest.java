package com.gym.crm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidatorTest {
    public static final Long ZERO_ID = 0L;
    public static final Long NEGATIVE_ID = -1L;
    public static final String VALID_STRING = "value";
    public static final String BLANK_STRING = " ";
    public static final String OBJECT_NAME = "Object Name";
    public static final String FIELD_NAME = "Field Name";

    public static final String INVALID_ID_MESSAGE = "ID must be positive and not null, got: %s";
    public static final String NULL_OBJECT_MESSAGE = "%s cannot be null";
    public static final String BLANK_FIELD_MESSAGE = "%s cannot be null or empty";

    @Test
    public void validateId_shouldNotThrowException_whenIdIsNotNull() {
        assertDoesNotThrow(() -> Validator.validateId(1L));
    }

    @Test
    public void validateId_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validator.validateId(null));

        assertEquals(String.format(INVALID_ID_MESSAGE, "null"), exception.getMessage());
    }

    @Test
    public void validateId_shouldThrowException_whenIdIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validator.validateId(ZERO_ID));

        assertEquals(String.format(INVALID_ID_MESSAGE, ZERO_ID), exception.getMessage());
    }

    @Test
    public void validateId_shouldThrowException_whenIdIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validator.validateId(NEGATIVE_ID));

        assertEquals(String.format(INVALID_ID_MESSAGE, NEGATIVE_ID), exception.getMessage());
    }

    @Test
    public void validateNotNull_shouldNotThrowException_whenIsNotNull() {
        assertDoesNotThrow(() -> Validator.validateNotNull(VALID_STRING, OBJECT_NAME));
    }

    @Test
    public void validateNotNull_shouldThrowException_whenIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validator.validateNotNull(null, OBJECT_NAME));

        assertEquals(String.format(NULL_OBJECT_MESSAGE, OBJECT_NAME), exception.getMessage());
    }

    @Test
    public void validateNotBlank_shouldNotThrowException_whenIsNotNull() {
        assertDoesNotThrow(() -> Validator.validateNotBlank(VALID_STRING, FIELD_NAME));
    }

    @Test
    public void validateNotBlank_shouldThrowException_whenIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validator.validateNotBlank(null, FIELD_NAME));

        assertEquals(String.format(BLANK_FIELD_MESSAGE, FIELD_NAME), exception.getMessage());
    }

    @Test
    public void validateNotBlank_shouldThrowException_whenIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Validator.validateNotBlank(BLANK_STRING, FIELD_NAME));

        assertEquals(String.format(BLANK_FIELD_MESSAGE, FIELD_NAME), exception.getMessage());
    }
}
