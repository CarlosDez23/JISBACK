package com.jis.training.domain.port.out;

import com.jis.training.domain.model.StatsPorMateria;
import com.jis.training.domain.model.StatsPorTopic;

import java.util.List;

public interface StatsRepositoryPort {
    List<StatsPorTopic> findStatsPorTopicByUsuarioId(Long usuarioId);
    List<StatsPorMateria> findStatsPorMateriaByUsuarioId(Long usuarioId);
}
