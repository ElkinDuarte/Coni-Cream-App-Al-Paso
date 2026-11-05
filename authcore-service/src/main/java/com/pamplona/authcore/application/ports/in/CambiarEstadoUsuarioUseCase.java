package com.pamplona.authcore.application.ports.in;

/**
 * Puerto de entrada: cambiar el estado activo/suspendido de un comerciante sin borrar historial.
 */
public interface CambiarEstadoUsuarioUseCase {
    void cambiarEstado(Long id, boolean activo);
}