package com.jis.training.domain.port.out;

import com.jis.training.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);
    boolean existsByCorreoElectronico(String correoElectronico);
    List<Usuario> findByComunidadId(Integer comunidadId);
    void updatePassword(Long id, String newPassword);
}
