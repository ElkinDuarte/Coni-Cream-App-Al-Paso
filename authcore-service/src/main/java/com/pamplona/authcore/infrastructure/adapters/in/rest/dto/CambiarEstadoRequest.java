package com.pamplona.authcore.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(
        @NotNull(message = "El campo activo es obligatorio (true para activar, false para suspender)")
        Boolean activo
) {}