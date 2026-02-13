package com.jis.training.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "Formato de email inválido")
        String correoElectronico,

        @NotNull(message = "La comunidad es obligatoria")
        Integer comunidadId,

        boolean isAdmin
) {}
