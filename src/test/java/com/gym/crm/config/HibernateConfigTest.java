package com.gym.crm.config;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
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
