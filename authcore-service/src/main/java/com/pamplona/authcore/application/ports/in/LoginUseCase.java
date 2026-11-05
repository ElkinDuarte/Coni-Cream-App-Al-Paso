package com.pamplona.authcore.application.ports.in;

/**
 * Puerto de entrada: autenticar un usuario mediante usuario o correo y obtener un JWT.
 */
public interface LoginUseCase {
    String login(String identifier, String rawPassword);
}