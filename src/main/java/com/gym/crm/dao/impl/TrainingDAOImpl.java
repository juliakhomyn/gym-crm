package com.gym.crm.dao.impl;

import com.gym.crm.config.TransactionManager;
import com.gym.crm.search.criteria.TraineeTrainingCriteriaBuilder;
import com.gym.crm.search.criteria.TrainerTrainingCriteriaBuilder;
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;
import com.gym.crm.dao.TrainingDAO;
import com.gym.crm.model.Training;
import com.gym.crm.util.Validator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainingDAOImpl implements TrainingDAO {

    private final TransactionManager transactionManager;
    private final TraineeTrainingCriteriaBuilder traineeCriteriaBuilder;
    private final TrainerTrainingCriteriaBuilder trainerCriteriaBuilder;

    public Training save(Training training) {
        Validator.validateNotNull(training, "Training");

        transactionManager.performWithinTx(manager -> manager.persist(training));

        return training;
    }

    public Optional<Training> findById(Long id) {
        Validator.validateId(id);

        return transactionManager.performReturningWithinTx(manager ->
                Optional.ofNullable(manager.find(Training.class, id)));
    }

    public List<Training> findAll() {
        return transactionManager.performReturningWithinTx(manager -> manager
                .createQuery("from Training", Training.class)
                .getResultList()
        );
    }

    @Override
    public List<Training> findByTraineeCriteria(TraineeTrainingFilter filter) {
        Validator.validateNotNull(filter, "Filter");

        return transactionManager.performReturningWithinTx(manager -> {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<Training> cq = traineeCriteriaBuilder.build(cb, filter);

            return manager.createQuery(cq).getResultList();
        });
    }

    @Override
    public List<Training> findByTrainerCriteria(TrainerTrainingFilter filter) {
        Validator.validateNotNull(filter, "Filter");

        return transactionManager.performReturningWithinTx(manager -> {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<Training> cq = trainerCriteriaBuilder.build(cb, filter);

            return manager.createQuery(cq).getResultList();
        });
    }
}
