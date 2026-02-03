package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.SimulacroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulacroRepository extends JpaRepository<SimulacroEntity, Long> {
}
