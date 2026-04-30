package com.gym.crm.dao.impl;

import com.gym.crm.config.TransactionManager;
import com.gym.crm.dao.TrainingTypeDAO;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.util.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainingTypeHibernateDAOImpl implements TrainingTypeDAO {

    private final TransactionManager transactionManager;

    @Override
    public Optional<TrainingType> findById(Long id) {
        Validator.validateId(id);

        return transactionManager.performReturningWithinTx(manager ->
                Optional.ofNullable(manager.find(TrainingType.class, id)));
    }

    @Override
    public Optional<TrainingType> findByTrainingTypeName(String name) {
        Validator.validateNotBlank(name, "Training Type Name");

        return transactionManager.performReturningWithinTx(manager -> manager
                .createQuery("SELECT t FROM TrainingType t WHERE t.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
        );
    }

    @Override
    public List<TrainingType> findAll() {
        return transactionManager.performReturningWithinTx(manager -> manager
                .createQuery("from TrainingType", TrainingType.class)
                .getResultList()
        );
    }
}
