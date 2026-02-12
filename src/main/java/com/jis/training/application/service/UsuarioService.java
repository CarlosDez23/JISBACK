package com.jis.training.application.service;

import com.jis.training.domain.model.Usuario;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService extends GenericCrudService<Usuario, Long> {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public UsuarioService(
            @Qualifier("usuarioPersistenceAdapter") PersistencePort<Usuario, Long> persistencePort,
            UsuarioRepositoryPort usuarioRepositoryPort) {
        super(persistencePort);
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    public Optional<Usuario> findByCorreoElectronico(String correoElectronico) {
        return usuarioRepositoryPort.findByCorreoElectronico(correoElectronico);
    }

    public boolean existsByCorreoElectronico(String correoElectronico) {
        return usuarioRepositoryPort.existsByCorreoElectronico(correoElectronico);
    }
}
