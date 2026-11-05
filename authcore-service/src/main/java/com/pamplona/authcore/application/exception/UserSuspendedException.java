package com.pamplona.authcore.application.exception;

/**
 * Excepción de aplicación: lanzada cuando un usuario con activo = false
 * intenta autenticarse en el sistema.
 */
public class UserSuspendedException extends RuntimeException {

    public UserSuspendedException(String identifier) {
        super("La cuenta asociada al usuario o correo '" + identifier + "' se encuentra suspendida.");
    }
}
