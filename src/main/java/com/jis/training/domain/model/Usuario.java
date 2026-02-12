package com.jis.training.domain.model;

import lombok.Data;

@Data
public class Usuario {
    private Long id;
    private String nombre;
    private String apellidos;
    private String correoElectronico;
    private String userPassword;
    private Comunidad comunidad;
}
