package com.gym.crm.repository.impl;

import com.gym.crm.exception.ValidationFailedException;
import com.gym.crm.model.Training;
import com.gym.crm.repository.TrainingRepositoryCriteria;
import com.gym.crm.search.criteria.TraineeTrainingCriteriaBuilder;
import com.gym.crm.search.criteria.TrainerTrainingCriteriaBuilder;
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@Import({TraineeTrainingCriteriaBuilder.class, TrainerTrainingCriteriaBuilder.class})
@RequiredArgsConstructor
public class TrainingRepositoryImpl implements TrainingRepositoryCriteria {
    private static final String FILTER_NOT_NULL = "Filter cannot be null";

    private final EntityManager entityManager;
    private final TraineeTrainingCriteriaBuilder traineeCriteriaBuilder;
    private final TrainerTrainingCriteriaBuilder trainerCriteriaBuilder;

    @Override
    public List<Training> findByTraineeCriteria(TraineeTrainingFilter filter) {
        if (Objects.isNull(filter)) {
            throw new ValidationFailedException(FILTER_NOT_NULL);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> cq = traineeCriteriaBuilder.build(cb, filter);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Training> findByTrainerCriteria(TrainerTrainingFilter filter) {
        if (Objects.isNull(filter)) {
            throw new ValidationFailedException(FILTER_NOT_NULL);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> cq = trainerCriteriaBuilder.build(cb, filter);

        return entityManager.createQuery(cq).getResultList();
    }
}
