package com.gym.crm.dao.impl;

import com.gym.crm.config.TransactionManager;
import com.gym.crm.dao.TrainerHibernateDAO;
import com.gym.crm.entity.Trainer;
import com.gym.crm.util.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainerHibernateDAOImpl implements TrainerHibernateDAO {

    private final TransactionManager transactionManager;

    public Trainer save(Trainer trainer) {
        Validator.validateNotNull(trainer, "Trainer");

        transactionManager.performWithinTx(manager -> manager.persist(trainer));

        return trainer;
    }

    public Trainer update(Trainer trainer) {
        Validator.validateId(trainer.getId());

        transactionManager.performWithinTx(manager -> manager.merge(trainer));

        return trainer;
    }

    public Optional<Trainer> findById(Long id) {
        Validator.validateId(id);

        return transactionManager.performReturningWithinTx(manager ->
                Optional.ofNullable(manager.find(Trainer.class, id)));
    }

    public List<Trainer> findAll() {
        return transactionManager.performReturningWithinTx(manager -> manager
                .createQuery("from Trainer", Trainer.class)
                .getResultList()
        );
    }
}
