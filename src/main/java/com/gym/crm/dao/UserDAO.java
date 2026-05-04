package com.gym.crm.dao;

import com.gym.crm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User save(User user);

    User update(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    boolean existsByUsername(String username);
}
