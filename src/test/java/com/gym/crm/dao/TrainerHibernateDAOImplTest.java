package com.gym.crm.dao;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.dao.impl.TrainerHibernateDAOImpl;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DatabaseSetup(value = "/dataset/trainer-dataset.xml")
public class TrainerHibernateDAOImplTest extends AbstractDaoTest<TrainerHibernateDAOImpl> {
    private static final String INVALID_ID_MESSAGE = "ID must be positive and not null, got: %s";

    @Test
    void save_shouldSaveTrainer_whenValid() {
        Trainer trainer = buildTrainer();

        Trainer actual = dao.save(trainer);

        assertThat(actual.getId()).isNotNull();
        assertThat(dao.findById(actual.getId())).isPresent();
        assertThat(actual.getUser().getUsername()).isEqualTo("Simone.Radcliffe");
        assertThat(actual.getUser().getFirstName()).isEqualTo("Simone");
        assertThat(actual.getUser().getLastName()).isEqualTo("Radcliffe");
        assertThat(actual.getUser().getIsActive()).isEqualTo(true);
        assertThat(actual.getSpecialization().getTrainingTypeName()).isEqualTo("Yoga");
    }

    @Test
    void save_shouldThrowException_whenSavingNullTrainer() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.save(null));

        assertThat(exception.getMessage()).isEqualTo("Trainer cannot be null");
    }

    @Test
    void update_shouldUpdateExistingTrainer_whenExists() {
        TrainingType newTrainingType = TrainingType.builder()
                .id(2L)
                .trainingTypeName("Pilates")
                .build();

        Trainer Trainer = dao.findById(1L).orElseThrow(() -> new AssertionError("Trainer not found"));
        Trainer updated = Trainer.toBuilder()
                .specialization(newTrainingType)
                .build();

        Trainer saved = dao.update(updated);
        Trainer actual = dao.findById(saved.getId()).orElseThrow(() -> new AssertionError("Trainer not found"));

        assertThat(actual.getSpecialization().getTrainingTypeName()).isEqualTo("Pilates");
    }

    @Test
    void update_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.update(buildTrainer()));

        assertThat(exception.getMessage()).isEqualTo(String.format(INVALID_ID_MESSAGE, "null"));
    }

    @Test
    void findById_shouldReturnTrainer_whenExists() {
        Optional<Trainer> actual = dao.findById(1L);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUser().getUsername()).isEqualTo("Callum.Whitfield");
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainer> actual = dao.findById(999L);

        assertThat(actual).isEmpty();
    }

    @Test
    void findById_shouldThrowException_whenIdIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findById(0L));

        assertThat(exception.getMessage()).isEqualTo(String.format(INVALID_ID_MESSAGE, "0"));
    }

    @Test
    void findAll_shouldReturnAllTrainers_whenExist() {
        List<Trainer> actual = dao.findAll();

        assertThat(actual)
                .hasSize(1)
                .extracting(t -> t.getUser().getUsername())
                .contains("Callum.Whitfield");
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .user(buildUser())
                .specialization(TrainingType.builder().id(1L).trainingTypeName("Yoga").build())
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
    protected Class<TrainerHibernateDAOImpl> getDaoClass() {
        return TrainerHibernateDAOImpl.class;
    }
}
