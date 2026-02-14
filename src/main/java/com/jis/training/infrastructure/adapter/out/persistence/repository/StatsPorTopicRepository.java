package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.StatsPorTopicEntity;
import com.jis.training.infrastructure.adapter.out.persistence.entity.StatsPorTopicId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatsPorTopicRepository extends JpaRepository<StatsPorTopicEntity, StatsPorTopicId> {
    List<StatsPorTopicEntity> findByUsuarioId(Long usuarioId);
}
