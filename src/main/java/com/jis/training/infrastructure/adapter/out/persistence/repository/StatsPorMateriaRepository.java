package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.StatsPorMateriaEntity;
import com.jis.training.infrastructure.adapter.out.persistence.entity.StatsPorMateriaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatsPorMateriaRepository extends JpaRepository<StatsPorMateriaEntity, StatsPorMateriaId> {
    List<StatsPorMateriaEntity> findByUsuarioId(Long usuarioId);
}
