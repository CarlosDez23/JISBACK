package com.jis.training.application.service;

import com.jis.training.domain.model.User;
import com.jis.training.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService extends GenericCrudService<User, Integer> {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        super(userRepositoryPort);
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepositoryPort.findByUsername(username);
    }

    @Override
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return super.create(user);
    }

    @Override
    public User update(Integer id, User user) {
        // Retrieve existing logic or just encode
        // This is a simple update implementation
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return super.update(id, user);
    }
}
