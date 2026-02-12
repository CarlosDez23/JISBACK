package com.jis.training.application.service;

import com.jis.training.domain.model.StatsPorMateria;
import com.jis.training.domain.model.StatsPorTopic;
import com.jis.training.domain.port.out.StatsRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepositoryPort statsRepositoryPort;

    public List<StatsPorTopic> getStatsPorTopicByUsuarioId(Long usuarioId) {
        return statsRepositoryPort.findStatsPorTopicByUsuarioId(usuarioId);
    }

    public List<StatsPorMateria> getStatsPorMateriaByUsuarioId(Long usuarioId) {
        return statsRepositoryPort.findStatsPorMateriaByUsuarioId(usuarioId);
    }
}
