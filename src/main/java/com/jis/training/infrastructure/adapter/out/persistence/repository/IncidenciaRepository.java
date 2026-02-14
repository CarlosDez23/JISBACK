package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.domain.model.EstadoIncidencia;
import com.jis.training.infrastructure.adapter.out.persistence.entity.IncidenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidenciaRepository extends JpaRepository<IncidenciaEntity, Long> {

    List<IncidenciaEntity> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    List<IncidenciaEntity> findByEstadoOrderByFechaCreacionDesc(EstadoIncidencia estado);

    List<IncidenciaEntity> findAllByOrderByFechaCreacionDesc();

    @Query("SELECT COUNT(i) FROM IncidenciaEntity i WHERE i.usuario.id = :usuarioId AND i.tieneNovedades = true")
    long countNovedadesByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE IncidenciaEntity i SET i.tieneNovedades = :tieneNovedades WHERE i.id = :id")
    void updateTieneNovedades(@Param("id") Long id, @Param("tieneNovedades") boolean tieneNovedades);

    @Modifying
    @Query("UPDATE IncidenciaEntity i SET i.estado = :estado, i.tieneNovedades = true WHERE i.id = :id")
    void updateEstado(@Param("id") Long id, @Param("estado") EstadoIncidencia estado);

    void deleteByUsuarioId(Long usuarioId);
}
