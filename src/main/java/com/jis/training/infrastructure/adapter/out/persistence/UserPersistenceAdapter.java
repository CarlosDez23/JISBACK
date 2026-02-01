package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.User;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.UserRepositoryPort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.UserEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserRepository repository;
    private final UserEntityMapper mapper;

    @Override
    public List<User> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(Integer id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public User save(User entity) {
        UserEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(mapper::toDomain);
    }
}
