package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.StatsPorTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatsPorTopicRepository extends JpaRepository<StatsPorTopicEntity, Long> {
    List<StatsPorTopicEntity> findByUsuarioId(Long usuarioId);
}
