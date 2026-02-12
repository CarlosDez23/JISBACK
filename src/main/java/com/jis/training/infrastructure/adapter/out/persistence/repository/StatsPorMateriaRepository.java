package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.StatsPorMateriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatsPorMateriaRepository extends JpaRepository<StatsPorMateriaEntity, Long> {
    List<StatsPorMateriaEntity> findByUsuarioId(Long usuarioId);
}
