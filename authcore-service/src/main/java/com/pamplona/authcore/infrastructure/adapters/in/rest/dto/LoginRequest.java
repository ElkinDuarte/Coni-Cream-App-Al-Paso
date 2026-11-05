package com.pamplona.authcore.infrastructure.adapters.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para autenticación.
 * Admite 'username', 'email' o 'identifier' indistintamente para soportar Login Dual.
 */
public record LoginRequest(
        @JsonAlias({"identifier", "email"})
        @NotBlank(message = "El usuario o correo electrónico es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
    public String identifier() {
        return username;
    }
}