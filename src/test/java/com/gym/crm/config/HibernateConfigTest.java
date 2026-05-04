package com.gym.crm.config;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(TestAppConfig.class)
public class HibernateConfigTest {

    @Autowired
    private EntityManagerFactory factory;

    @Test
    void shouldCreateEntityManagerFactory() {
        assertNotNull(factory);
    }

    @Test
    void shouldOpenSession() {
        try (EntityManager manager = factory.createEntityManager()) {
            assertNotNull(manager);
            assertTrue(manager.isOpen());
        }
    }

    @Test
    void shouldContainAllEntities() {
        var metamodel = factory.getMetamodel();

        assertNotNull(metamodel.entity(User.class));
        assertNotNull(metamodel.entity(Trainee.class));
        assertNotNull(metamodel.entity(Trainer.class));
        assertNotNull(metamodel.entity(Training.class));
        assertNotNull(metamodel.entity(TrainingType.class));
    }
}
