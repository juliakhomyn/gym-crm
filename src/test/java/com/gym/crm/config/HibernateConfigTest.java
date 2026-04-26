package com.gym.crm.config;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
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
@ContextConfiguration(classes = {TestAppConfig.class, DataSourceConfig.class, LiquibaseConfig.class, HibernateConfig.class})
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
