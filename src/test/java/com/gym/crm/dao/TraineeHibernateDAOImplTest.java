package com.gym.crm.dao;

import com.gym.crm.dao.impl.TraineeHibernateDAOImpl;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TraineeHibernateDAOImplTest extends AbstractDaoTest<TraineeHibernateDAOImpl> {
    private static final String INVALID_ID_MESSAGE = "ID must be positive and not null, got: %s";

    @Test
    void save_shouldSaveTrainee_whenValid() {
        Trainee trainee = buildTrainee();

        Trainee actual = dao.save(trainee);

        assertNotNull(actual.getId());
        assertTrue(dao.findById(actual.getId()).isPresent());
    }

    @Test
    void save_shouldThrowException_whenSavingNullTrainee() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.save(null));

        assertEquals("Trainee cannot be null", exception.getMessage());
    }

    @Test
    void update_shouldUpdateExistingTrainee_whenExists() {
        Trainee trainee = dao.findById(1L).orElseThrow(() -> new AssertionError("Trainee not found"));
        Trainee updated = trainee.toBuilder().address("new address").build();

        Trainee saved = dao.update(updated);
        Trainee actual = dao.findById(saved.getId()).orElseThrow(() -> new AssertionError("Trainee not found"));

        assertEquals("new address", actual.getAddress());
    }

    @Test
    void update_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.update(buildTrainee()));

        assertEquals(String.format(INVALID_ID_MESSAGE, "null"), exception.getMessage());
    }

    @Test
    void delete_shouldDeleteTrainee_whenExists() {
        dao.delete(1L);

        assertTrue(dao.findById(1L).isEmpty());
        assertEquals(1, dao.findAll().size());
    }

    @Test
    void delete_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.delete(null));

        assertEquals(String.format(INVALID_ID_MESSAGE, "null"), exception.getMessage());
    }


    @Test
    void findById_shouldReturnTrainee_whenExists() {
        Optional<Trainee> actual = dao.findById(1L);

        assertTrue(actual.isPresent());
        assertEquals("Nora.Pemberton", actual.get().getUser().getUsername());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainee> actual = dao.findById(999L);

        assertTrue(actual.isEmpty());
    }

    @Test
    void findById_shouldThrowException_whenIdIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findById(0L));

        assertEquals(String.format(INVALID_ID_MESSAGE, "0"), exception.getMessage());
    }

    @Test
    void findAll_shouldReturnAllTrainees_whenExist() {
        List<Trainee> actual = dao.findAll();

        assertEquals(2, actual.size());
        assertThat(actual)
                .hasSize(2)
                .extracting(t -> t.getUser().getUsername())
                .containsExactlyInAnyOrder("Nora.Pemberton", "Ellis.Hargrove");
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .user(buildUser())
                .dateOfBirth(LocalDate.of(2000, 3, 10))
                .address("123 Main St")
                .build();
    }

    private User buildUser() {
        return User.builder()
                .firstName("Simone")
                .lastName("Radcliffe")
                .username("Simone.Radcliffe")
                .password("pass444")
                .isActive(true)
                .build();
    }

    @Override
    protected Class<TraineeHibernateDAOImpl> getDaoClass() {
        return TraineeHibernateDAOImpl.class;
    }
}
