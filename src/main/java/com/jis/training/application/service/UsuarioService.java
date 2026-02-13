package com.jis.training.application.service;

import com.jis.training.domain.model.Usuario;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService extends GenericCrudService<Usuario, Long> {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private static final String PASSWORD = "password";

    public UsuarioService(
            @Qualifier("usuarioPersistenceAdapter") PersistencePort<Usuario, Long> persistencePort,
            UsuarioRepositoryPort usuarioRepositoryPort) {
        super(persistencePort);
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }


    @Override
    public Usuario update(Long id, Usuario usuario) {
        getById(id).ifPresent(value -> {
            if (PASSWORD.equalsIgnoreCase(usuario.getUserPassword())) {
                usuario.setUserPassword(value.getUserPassword());
            }
            usuario.setPasswordChangeRequired(value.isPasswordChangeRequired());
        });
        return super.update(id, usuario);
    }

    public Optional<Usuario> findByCorreoElectronico(String correoElectronico) {
        return usuarioRepositoryPort.findByCorreoElectronico(correoElectronico);
    }

    public boolean existsByCorreoElectronico(String correoElectronico) {
        return usuarioRepositoryPort.existsByCorreoElectronico(correoElectronico);
    }

    public List<Usuario> findByComunidadId(Integer comunidadId) {
        return usuarioRepositoryPort.findByComunidadId(comunidadId);
    }

    public void updatePassword(Long id, String newPassword) {
        usuarioRepositoryPort.updatePassword(id, newPassword);
    }
}
