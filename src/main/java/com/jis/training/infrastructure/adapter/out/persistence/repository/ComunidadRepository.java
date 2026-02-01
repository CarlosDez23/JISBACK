package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.ComunidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComunidadRepository extends JpaRepository<ComunidadEntity, Integer> {
}
