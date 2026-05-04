package com.gym.crm.dao.impl;

import com.gym.crm.config.TransactionManager;
import com.gym.crm.dao.UserDAO;
import com.gym.crm.model.User;
import com.gym.crm.util.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDAOImpl implements UserDAO {

    private final TransactionManager transactionManager;

    @Override
    public User save(User user) {
        Validator.validateNotNull(user, "User");

        transactionManager.performWithinTx(manager -> manager.persist(user));

        return user;
    }

    @Override
    public User update(User user) {
        Validator.validateId(user.getId());

        transactionManager.performWithinTx(manager -> manager.merge(user));

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        Validator.validateId(id);

        return transactionManager.performReturningWithinTx(manager ->
                Optional.ofNullable(manager.find(User.class, id)));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Validator.validateNotBlank(username, "Username");

        return transactionManager.performReturningWithinTx(manager ->
                manager.createQuery("FROM User u WHERE u.username = :username", User.class)
                        .setParameter("username", username)
                        .getResultStream()
                        .findFirst()
        );
    }

    @Override
    public List<User> findAll() {
        return transactionManager.performReturningWithinTx(manager ->
                manager.createQuery("from User", User.class)
                        .getResultList());
    }

    @Override
    public boolean existsByUsername(String username) {
        Validator.validateNotBlank(username, "Username");

        return transactionManager.performReturningWithinTx(manager ->
                manager.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                        .setParameter("username", username)
                        .getSingleResult() > 0);
    }
}
