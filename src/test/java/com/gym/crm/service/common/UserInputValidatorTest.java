package com.gym.crm.service.common;

import com.gym.crm.exception.ValidationFailedException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserInputValidatorTest {
    private static final String OBJECT_NAME = "User";
    private static final String FIELD_NAME = "Username";

    @Mock
    private Validator validator;
    @Mock
    private ConstraintViolation<Object> violation;

    @InjectMocks
    private UserInputValidator userInputValidator;

    @Test
    void validate_shouldThrowException_whenObjectIsNull() {
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> userInputValidator.validate(null, OBJECT_NAME));

        assertThat(exception.getMessage()).isEqualTo("User cannot be null");
    }

    @Test
    void validate_shouldPass_whenNoViolationsExist() {
        Object object = new Object();

        when(validator.validate(object)).thenReturn(Collections.emptySet());

        assertDoesNotThrow(() -> userInputValidator.validate(object, OBJECT_NAME));
        verify(validator).validate(object);
    }

    @Test
    void validate_shouldThrowException_whenViolationsExist() {
        Object object = new Object();
        Path propertyPath = PathImpl.createPathFromString("username");
        Set<ConstraintViolation<Object>> violations = Set.of(violation);

        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn("must not be blank");
        when(validator.validate(object)).thenReturn(violations);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> userInputValidator.validate(object, OBJECT_NAME));

        assertThat(exception.getMessage()).isEqualTo("Validation failed: username: must not be blank");
    }

    @Test
    void validateUsername_shouldThrowException_whenUsernameIsNull() {
        ValidationFailedException exception = assertThrows(
                ValidationFailedException.class,
                () -> userInputValidator.validateUsername(null)
        );

        assertThat(exception.getMessage()).isEqualTo("Username cannot be null or empty");
    }

    @Test
    void validateUsername_shouldThrowException_whenUsernameIsBlank() {
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> userInputValidator.validateUsername("   "));

        assertThat(exception.getMessage()).isEqualTo("Username cannot be null or empty");
    }

    @Test
    void validateUsername_shouldPass_whenUsernameIsValid() {
        assertDoesNotThrow(() -> userInputValidator.validateUsername("Nora.Pemberton"));
    }

    @Test
    void validateId_shouldThrowException_whenIdIsNull() {
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> userInputValidator.validateId(null));

        assertThat(exception.getMessage()).isEqualTo("ID cannot be null");
    }

    @Test
    void validateId_shouldThrowException_whenIdIsZero() {
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> userInputValidator.validateId(0L));

        assertThat(exception.getMessage()).isEqualTo("ID must be a positive number");
    }

    @Test
    void validateId_shouldThrowException_whenIdIsNegative() {
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> userInputValidator.validateId(-1L));

        assertThat(exception.getMessage()).isEqualTo("ID must be a positive number");
    }

    @Test
    void validateId_shouldPass_whenIdIsPositive() {
        assertDoesNotThrow(() -> userInputValidator.validateId(1L));
    }

    @Test
    void validateNotBlank_shouldThrowException_whenValueIsNull() {
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> userInputValidator.validateNotBlank(null, FIELD_NAME));

        assertThat(exception.getMessage()).isEqualTo("Username cannot be null");
    }

    @Test
    void validateNotBlank_shouldThrowException_whenValueIsEmpty() {
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> userInputValidator.validateNotBlank("", FIELD_NAME));

        assertThat(exception.getMessage()).isEqualTo("Username cannot be empty");
    }

    @Test
    void validateNotBlank_shouldThrowException_whenValueIsBlank() {
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> userInputValidator.validateNotBlank("   ", FIELD_NAME));

        assertThat(exception.getMessage()).isEqualTo("Username cannot be empty");
    }

    @Test
    void validateNotBlank_shouldPass_whenValueIsValid() {
        assertDoesNotThrow(() -> userInputValidator.validateNotBlank("Nora", FIELD_NAME));
    }
}
