package com.pamplona.authcore.infrastructure.adapters.in.rest.dto;

import com.pamplona.authcore.domain.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotNull(message = "El tipo de documento es obligatorio (CC, CE, PPT, PAS)")
        TipoDocumento tipoDocumento,

        @NotBlank(message = "El número de documento es obligatorio")
        String numeroDocumento,

        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "Formato de correo electrónico inválido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotBlank(message = "El teléfono es obligatorio")
        String telefono
) {}