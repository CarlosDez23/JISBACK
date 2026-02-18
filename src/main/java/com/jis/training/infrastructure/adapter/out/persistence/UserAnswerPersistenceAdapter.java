package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.UserAnswer;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.UserAnswerRepositoryPort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.UserAnswerEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.UserAnswerEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("userAnswerPersistenceAdapter")
@RequiredArgsConstructor
public class UserAnswerPersistenceAdapter implements PersistencePort<UserAnswer, Long>, UserAnswerRepositoryPort {

    private final UserAnswerRepository repository;
    private final UserAnswerEntityMapper mapper;

    @Override
    public List<UserAnswer> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserAnswer> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public UserAnswer save(UserAnswer entity) {
        UserAnswerEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<UserAnswer> findByUsuarioId(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserAnswer> findByQuestionId(Long questionId) {
        return repository.findByQuestionId(questionId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserAnswer> saveAll(List<UserAnswer> answers) {
        List<UserAnswerEntity> entities = answers.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        return repository.saveAll(entities).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByQuestionIds(List<Long> questionIds) {
        if (!questionIds.isEmpty()) {
            repository.deleteByQuestionIdIn(questionIds);
        }
    }
}
