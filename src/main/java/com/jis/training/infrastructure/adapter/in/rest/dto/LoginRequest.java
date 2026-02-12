package com.jis.training.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "Formato de email inválido")
        String correoElectronico,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
