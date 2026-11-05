package com.pamplona.authcore.infrastructure.adapters.in.rest.dto;

import com.pamplona.authcore.domain.Role;
import com.pamplona.authcore.domain.TipoDocumento;
import com.pamplona.authcore.domain.User;

import java.util.Set;

public record UserResponse(
        Long id,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        String username,
        String email,
        String nombre,
        String apellido,
        String telefono,
        boolean activo,
        Set<Role> roles
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getTipoDocumento(),
                user.getNumeroDocumento(),
                user.getUsername(),
                user.getEmail(),
                user.getNombre(),
                user.getApellido(),
                user.getTelefono(),
                user.isActivo(),
                user.getRoles()
        );
    }
}