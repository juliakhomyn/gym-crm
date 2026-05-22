package com.gym.crm;

import com.gym.crm.config.TestAppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(TestAppConfig.class)
class GymAppTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void context_shouldLoadSuccessfully() {
        assertThat(context).isNotNull();
    }
}
