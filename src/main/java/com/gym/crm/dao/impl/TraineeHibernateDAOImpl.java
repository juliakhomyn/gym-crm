package com.gym.crm.dao.impl;

import com.gym.crm.config.TransactionManager;
import com.gym.crm.dao.TraineeHibernateDAO;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.util.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TraineeHibernateDAOImpl implements TraineeHibernateDAO {

    private final TransactionManager transactionManager;

    @Override
    public Trainee save(Trainee trainee) {
        Validator.validateNotNull(trainee, "Trainee");

        transactionManager.performWithinTx(manager -> manager.persist(trainee));

        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Validator.validateId(trainee.getId());

        transactionManager.performWithinTx(manager -> manager.merge(trainee));

        return trainee;
    }

    @Override
    public void delete(Long id) {
        Validator.validateId(id);

        transactionManager.performWithinTx(manager -> {
            Trainee trainee = manager.find(Trainee.class, id);

            if (trainee != null) {
                manager.remove(trainee);
            }
        });
    }

    @Override
    public void deleteByUsername(String username) {
        Validator.validateNotBlank(username, "Username");
        transactionManager.performWithinTx(manager ->
                manager.createQuery(
                                "FROM Trainee t JOIN FETCH t.user WHERE t.user.username = :username",
                                Trainee.class)
                        .setParameter("username", username)
                        .getResultStream()
                        .findFirst()
                        .ifPresent(manager::remove));
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        Validator.validateId(id);

        return transactionManager.performReturningWithinTx(manager ->
                Optional.ofNullable(manager.find(Trainee.class, id)));
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        Validator.validateNotBlank(username, "Username");

        return transactionManager.performReturningWithinTx(manager ->
                manager.createQuery("FROM Trainee t JOIN FETCH t.user WHERE t.user.username = :username", Trainee.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
        );
    }

    @Override
    public List<Trainee> findAll() {
        return transactionManager.performReturningWithinTx(manager -> manager
                .createQuery("from Trainee", Trainee.class)
                .getResultList()
        );
    }

    @Override
    public void updateTrainersList(String username, List<Trainer> trainers) {
        Validator.validateNotBlank(username, "Username");
        Validator.validateNotNull(trainers, "Trainers");

        transactionManager.performWithinTx(manager -> {
            Trainee trainee = manager.createQuery(
                            "FROM Trainee t " +
                                    "JOIN FETCH t.user " +
                                    "LEFT JOIN FETCH t.trainers " +
                                    "WHERE t.user.username = :username",
                            Trainee.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));

            List<Trainer> managedTrainers = trainers.stream()
                    .map(trainer -> manager.find(Trainer.class, trainer.getId()))
                    .filter(Objects::nonNull)
                    .toList();

            trainee.getTrainers().clear();
            trainee.getTrainers().addAll(managedTrainers);

            manager.merge(trainee);
        });
    }

    @Override
    public Optional<Trainee> findByUsernameWithTrainers(String username) {
        return transactionManager.performReturningWithinTx(manager ->
                manager.createQuery(
                                "FROM Trainee t " +
                                        "JOIN FETCH t.user " +
                                        "LEFT JOIN FETCH t.trainers tr " +
                                        "LEFT JOIN FETCH tr.user " +
                                        "WHERE t.user.username = :username",
                                Trainee.class)
                        .setParameter("username", username)
                        .getResultStream()
                        .findFirst());
    }
}
