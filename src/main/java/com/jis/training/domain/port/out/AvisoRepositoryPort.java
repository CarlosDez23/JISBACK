package com.jis.training.domain.port.out;

import com.jis.training.domain.model.Aviso;

import java.util.List;

public interface AvisoRepositoryPort {
    List<Aviso> findByComunidadId(Integer comunidadId);
    List<Aviso> findAvisosGenerales();
    List<Aviso> findAvisosParaUsuario(Integer comunidadId);
    List<Aviso> findActivos();
}
