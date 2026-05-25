package com.gym.crm.dao.impl;

import com.gym.crm.config.TransactionManager;
import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.model.Trainer;
import com.gym.crm.util.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainerDAOImpl implements TrainerDAO {

    private final TransactionManager transactionManager;

    @Override
    public Trainer save(Trainer trainer) {
        Validator.validateNotNull(trainer, "Trainer");

        transactionManager.performWithinTx(manager -> manager.persist(trainer));

        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        Validator.validateId(trainer.getId());

        transactionManager.performWithinTx(manager -> manager.merge(trainer));

        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        Validator.validateId(id);

        return transactionManager.performReturningWithinTx(manager ->
                Optional.ofNullable(manager.find(Trainer.class, id)));
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        Validator.validateNotBlank(username, "Username");

        return transactionManager.performReturningWithinTx(manager ->
            manager.createQuery("FROM Trainer t JOIN FETCH t.user WHERE t.user.username = :username", Trainer.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
        );
    }

    @Override
    public List<Trainer> findAll() {
        return transactionManager.performReturningWithinTx(manager -> manager
                .createQuery("from Trainer", Trainer.class)
                .getResultList()
        );
    }

    @Override
    public List<Trainer> findNotAssignedToTrainee(String traineeUsername) {
        Validator.validateNotBlank(traineeUsername, "Trainee Username");

        return transactionManager.performReturningWithinTx(manager ->
                manager.createQuery("SELECT DISTINCT t FROM Trainer t " +
                                        "LEFT JOIN FETCH t.user " +
                                        "LEFT JOIN FETCH t.trainees tr " +
                                        "LEFT JOIN FETCH tr.user " +
                                        "WHERE t NOT IN (" +
                                        "  SELECT tr2 FROM Trainee trn " +
                                        "  JOIN trn.trainers tr2 " +
                                        "  WHERE trn.user.username = :username)",
                                Trainer.class)
                    .setParameter("username", traineeUsername)
                    .getResultList()
        );
    }

    @Override
    public Optional<Trainer> findByUsernameWithTrainees(String username) {
        Validator.validateNotBlank(username, "Username");

        return transactionManager.performReturningWithinTx(manager ->
                manager.createQuery(
                                "FROM Trainer t " +
                                        "JOIN FETCH t.user " +
                                        "LEFT JOIN FETCH t.trainees trn " +
                                        "LEFT JOIN FETCH trn.user " +
                                        "WHERE t.user.username = :username",
                                Trainer.class)
                        .setParameter("username", username)
                        .getResultStream()
                        .findFirst()
        );
    }
}
