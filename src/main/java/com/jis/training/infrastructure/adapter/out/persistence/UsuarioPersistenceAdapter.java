package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.Usuario;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.UsuarioRepositoryPort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.UsuarioEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("usuarioPersistenceAdapter")
@RequiredArgsConstructor
public class UsuarioPersistenceAdapter implements PersistencePort<Usuario, Long>, UsuarioRepositoryPort {

    private final UsuarioRepository repository;
    private final UsuarioEntityMapper mapper;

    @Override
    public List<Usuario> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Usuario save(Usuario entity) {
        UsuarioEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Usuario> findByCorreoElectronico(String correoElectronico) {
        return repository.findByCorreoElectronico(correoElectronico).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return repository.existsByCorreoElectronico(correoElectronico);
    }
}
