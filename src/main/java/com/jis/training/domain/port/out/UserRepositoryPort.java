package com.jis.training.domain.port.out;

import com.jis.training.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort extends PersistencePort<User, Integer> {
    Optional<User> findByUsername(String username);
}
