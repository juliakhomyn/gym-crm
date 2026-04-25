package com.gym.crm.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HibernateConfig.class)
public class HibernateConfigTest {

    @Autowired
    private SessionFactory factory;

    @Test
    void shouldCreateSessionFactory() {
        assertNotNull(factory);
    }

    @Test
    void shouldOpenSession() {
        try (Session session = factory.openSession()) {
            assertNotNull(session);
            assertTrue(session.isOpen());
        }
    }
}
