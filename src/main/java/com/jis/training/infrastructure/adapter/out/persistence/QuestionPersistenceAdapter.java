package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.Answer;
import com.jis.training.domain.model.Question;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.QuestionRepositoryPort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.QuestionEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.AnswerEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.QuestionEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.AnswerRepository;
import com.jis.training.infrastructure.adapter.out.persistence.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QuestionPersistenceAdapter implements PersistencePort<Question, Long>, QuestionRepositoryPort {

    private final QuestionRepository repository;
    private final AnswerRepository answerRepository;
    private final QuestionEntityMapper mapper;
    private final AnswerEntityMapper answerMapper;

    @Override
    public List<Question> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Question> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Question save(Question entity) {
        QuestionEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Question> findByTopicIds(List<Long> topicIds) {
        return repository.findByTopicIdIn(topicIds).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Question> findByTopicId(Long topicId) {
        return repository.findByTopicId(topicId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Answer> findAnswersByQuestionIds(List<Long> questionIds) {
        return answerRepository.findByQuestionIdIn(questionIds).stream()
                .map(answerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByTopicId(Long topicId) {
        return repository.countByTopicId(topicId);
    }
}
