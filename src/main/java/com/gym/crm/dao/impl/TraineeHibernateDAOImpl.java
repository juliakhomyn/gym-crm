package com.gym.crm.dao.impl;

import com.gym.crm.config.TransactionManager;
import com.gym.crm.entity.Trainee;
import com.gym.crm.util.Validator;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeHibernateDAOImpl {

    private final TransactionManager transactionManager;

    public TraineeHibernateDAOImpl(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Trainee save(Trainee trainee) {
        Validator.validateNotNull(trainee, "Trainee");

        transactionManager.performWithinTx(session -> session.persist(trainee));

        return trainee;
    }

    public Trainee update(Trainee trainee) {
        Validator.validateId(trainee.getId());

        transactionManager.performWithinTx(session -> session.merge(trainee));

        return trainee;
    }

    public void delete(Long id) {
        Validator.validateId(id);

        Trainee trainee = transactionManager.performReturningWithinTx(session -> session.get(Trainee.class, id));
        if (trainee != null) {
            transactionManager.performWithinTx(session -> session.remove(trainee));
        }
    }

    public Optional<Trainee> findById(Long id) {
        Validator.validateId(id);

        return transactionManager.performReturningWithinTx(session ->
                Optional.ofNullable(session.get(Trainee.class, id)));
    }

    public List<Trainee> findAll() {
        return transactionManager.performReturningWithinTx(session -> session
                .createQuery("from Trainee", Trainee.class)
                .getResultList()
        );
    }
}
