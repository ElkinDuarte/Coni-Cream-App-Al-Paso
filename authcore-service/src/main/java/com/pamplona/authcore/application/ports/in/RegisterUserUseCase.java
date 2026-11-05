package com.pamplona.authcore.application.ports.in;

import com.pamplona.authcore.domain.TipoDocumento;

/**
 * Puerto de entrada: registro formal de comerciantes y usuarios.
 */
public interface RegisterUserUseCase {

    Long register(RegisterUserCommand command);

    record RegisterUserCommand(
            TipoDocumento tipoDocumento,
            String numeroDocumento,
            String username,
            String email,
            String rawPassword,
            String nombre,
            String apellido,
            String telefono
    ) {}
}