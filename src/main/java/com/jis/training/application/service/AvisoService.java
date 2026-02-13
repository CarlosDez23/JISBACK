package com.jis.training.application.service;

import com.jis.training.domain.model.Aviso;
import com.jis.training.domain.port.out.AvisoRepositoryPort;
import com.jis.training.domain.port.out.PersistencePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvisoService extends GenericCrudService<Aviso, Long> {

    private final AvisoRepositoryPort avisoRepositoryPort;

    public AvisoService(
            @Qualifier("avisoPersistenceAdapter") PersistencePort<Aviso, Long> persistencePort,
            AvisoRepositoryPort avisoRepositoryPort) {
        super(persistencePort);
        this.avisoRepositoryPort = avisoRepositoryPort;
    }

    public List<Aviso> findByComunidadId(Integer comunidadId) {
        return avisoRepositoryPort.findByComunidadId(comunidadId);
    }

    public List<Aviso> findAvisosGenerales() {
        return avisoRepositoryPort.findAvisosGenerales();
    }

    public List<Aviso> findAvisosParaUsuario(Integer comunidadId) {
        return avisoRepositoryPort.findAvisosParaUsuario(comunidadId);
    }

    public List<Aviso> findActivos() {
        return avisoRepositoryPort.findActivos();
    }
}
