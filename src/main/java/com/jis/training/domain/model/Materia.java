package com.jis.training.domain.model;

import lombok.Data;

@Data
public class Materia {
    private Integer id;
    private String nombreMateria;
    private String sigla;
    private Comunidad comunidad;
}
