package com.jis.training.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Aviso {
    private Long id;
    private String tituloAviso;
    private String cuerpoAviso;
    private Comunidad comunidad;
    private LocalDateTime fechaCreacion;
    private boolean activo;
}
