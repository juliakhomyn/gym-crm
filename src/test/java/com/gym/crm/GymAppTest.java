package com.gym.crm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=QWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo1Njc4OTAxMjM0NTY3OA==",
        "jwt.expiration=360000"
})
class GymAppTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void context_shouldLoadSuccessfully() {
        assertThat(context).isNotNull();
    }
}
