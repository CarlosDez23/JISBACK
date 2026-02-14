package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.IncidenciaComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaComentarioRepository extends JpaRepository<IncidenciaComentarioEntity, Long> {

    List<IncidenciaComentarioEntity> findByIncidenciaIdOrderByFechaCreacionAsc(Long incidenciaId);

    void deleteByUsuarioId(Long usuarioId);
}
