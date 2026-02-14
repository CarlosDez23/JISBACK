package com.jis.training.domain.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Incidencia {
    private Long id;
    private String titulo;
    private String descripcion;
    private String imagenUrl;
    private Usuario usuario;
    private LocalDateTime fechaCreacion;
    private EstadoIncidencia estado;
    private boolean tieneNovedades;
    private List<IncidenciaComentario> comentarios;
}
