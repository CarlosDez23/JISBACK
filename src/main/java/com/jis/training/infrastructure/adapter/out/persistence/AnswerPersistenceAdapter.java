package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.Answer;
import com.jis.training.domain.port.out.AnswerRepositoryPort;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.AnswerEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.AnswerEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AnswerPersistenceAdapter implements PersistencePort<Answer, Long>, AnswerRepositoryPort {

    private final AnswerRepository repository;
    private final AnswerEntityMapper mapper;

    @Override
    public List<Answer> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Answer> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Answer save(Answer entity) {
        AnswerEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Answer> findByQuestionId(Long questionId) {
        return repository.findByQuestionId(questionId).stream()
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
