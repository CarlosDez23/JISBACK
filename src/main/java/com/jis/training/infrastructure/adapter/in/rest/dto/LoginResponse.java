package com.jis.training.infrastructure.adapter.in.rest.dto;

public record LoginResponse(
        String token,
        Long userId,
        String correoElectronico,
        String nombre,
        String apellidos,
        boolean isAdmin,
        boolean passwordChangeRequired
) {}
