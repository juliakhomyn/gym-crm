package com.gym.crm.service.common;

import com.gym.crm.dao.UserDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameGeneratorTest {
    private static final String FIRST_NAME = "Cillian";
    private static final String LAST_NAME = "Mercer";
    private static final String USERNAME = "Cillian.Mercer";
    private static final String USERNAME_WITH_SUFFIX_1 = "Cillian.Mercer1";
    private static final String USERNAME_WITH_SUFFIX_2 = "Cillian.Mercer2";

    private static final String FIRST_NAME_FIELD = "First name";
    private static final String LAST_NAME_FIELD = "Last name";
    
    @Mock
    private UserDAO dao;
    @Mock
    private UserInputValidator validator;

    @InjectMocks
    private UsernameGenerator generator;

    @Test
    void generateUsername_shouldReturnConcatenatedUsername_whenNoDuplicates() {
        when(dao.existsByUsername(USERNAME)).thenReturn(false);

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertThat(actual).isEqualTo(USERNAME);
        verify(validator).validateNotBlank(FIRST_NAME, FIRST_NAME_FIELD);
        verify(validator).validateNotBlank(LAST_NAME, LAST_NAME_FIELD);
    }

    @Test
    void generateUsername_shouldReturnUsernameWithSuffix_whenDuplicateExists() {
        when(dao.existsByUsername(USERNAME)).thenReturn(true);
        when(dao.existsByUsername(USERNAME_WITH_SUFFIX_1)).thenReturn(false);

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertThat(actual).isEqualTo(USERNAME_WITH_SUFFIX_1);
        verify(validator).validateNotBlank(FIRST_NAME, FIRST_NAME_FIELD);
        verify(validator).validateNotBlank(LAST_NAME, LAST_NAME_FIELD);
    }

    @Test
    void generateUsername_shouldReturnUsernameWithSuffix2_whenTwoDuplicatesExist() {
        when(dao.existsByUsername(USERNAME)).thenReturn(true);
        when(dao.existsByUsername(USERNAME_WITH_SUFFIX_1)).thenReturn(true);
        when(dao.existsByUsername(USERNAME_WITH_SUFFIX_2)).thenReturn(false);

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertThat(actual).isEqualTo(USERNAME_WITH_SUFFIX_2);
        verify(validator).validateNotBlank(FIRST_NAME, FIRST_NAME_FIELD);
        verify(validator).validateNotBlank(LAST_NAME, LAST_NAME_FIELD);
    }
}
