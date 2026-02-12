package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByCorreoElectronico(String correoElectronico);
    boolean existsByCorreoElectronico(String correoElectronico);
    List<UsuarioEntity> findByComunidadId(Integer comunidadId);

    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.userPassword = :password, u.passwordChangeRequired = :pcr WHERE u.id = :id")
    void updatePasswordAndChangeRequired(@Param("id") Long id, @Param("password") String password, @Param("pcr") boolean passwordChangeRequired);
}
