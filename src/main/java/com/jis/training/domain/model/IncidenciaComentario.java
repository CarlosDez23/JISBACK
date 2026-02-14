package com.jis.training.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidenciaComentario {
    private Long id;
    private Incidencia incidencia;
    private Usuario usuario;
    private String texto;
    private LocalDateTime fechaCreacion;
}
