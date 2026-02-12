package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.AvisoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AvisoRepository extends JpaRepository<AvisoEntity, Long> {

    List<AvisoEntity> findByComunidadId(Integer comunidadId);

    @Query("SELECT a FROM AvisoEntity a WHERE a.comunidad IS NULL")
    List<AvisoEntity> findAvisosGenerales();

    @Query("SELECT a FROM AvisoEntity a WHERE a.activo = true AND (a.comunidad IS NULL OR a.comunidad.id = :comunidadId) ORDER BY a.fechaCreacion DESC")
    List<AvisoEntity> findAvisosActivosByComunidadIdOrGenerales(@Param("comunidadId") Integer comunidadId);

    List<AvisoEntity> findByActivoTrue();
}
