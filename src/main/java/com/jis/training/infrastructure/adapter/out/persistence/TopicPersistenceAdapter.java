package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.Topic;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.TopicRepositoryPort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.TopicEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.TopicEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TopicPersistenceAdapter implements PersistencePort<Topic, Long>, TopicRepositoryPort {

    private final TopicRepository repository;
    private final TopicEntityMapper mapper;

    @Override
    public List<Topic> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Topic> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Topic save(Topic entity) {
        TopicEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Topic> findByMateriaId(Long materiaId) {
        return repository.findByMateriaId(materiaId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
