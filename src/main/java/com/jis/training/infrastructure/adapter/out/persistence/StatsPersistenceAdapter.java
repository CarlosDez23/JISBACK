package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.StatsPorMateria;
import com.jis.training.domain.model.StatsPorTopic;
import com.jis.training.domain.port.out.StatsRepositoryPort;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.StatsPorMateriaEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.StatsPorTopicEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.StatsPorMateriaRepository;
import com.jis.training.infrastructure.adapter.out.persistence.repository.StatsPorTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StatsPersistenceAdapter implements StatsRepositoryPort {

    private final StatsPorTopicRepository statsPorTopicRepository;
    private final StatsPorMateriaRepository statsPorMateriaRepository;
    private final StatsPorTopicEntityMapper statsPorTopicMapper;
    private final StatsPorMateriaEntityMapper statsPorMateriaMapper;

    @Override
    public List<StatsPorTopic> findStatsPorTopicByUsuarioId(Long usuarioId) {
        return statsPorTopicRepository.findByUsuarioId(usuarioId).stream()
                .map(statsPorTopicMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<StatsPorMateria> findStatsPorMateriaByUsuarioId(Long usuarioId) {
        return statsPorMateriaRepository.findByUsuarioId(usuarioId).stream()
                .map(statsPorMateriaMapper::toDomain)
                .collect(Collectors.toList());
    }
}
