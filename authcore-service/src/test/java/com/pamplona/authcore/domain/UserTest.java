package com.pamplona.authcore.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Dominio: User")
class UserTest {

    @Test
    @DisplayName("Debe permitir el inicio de sesión cuando el usuario está activo")
    void testUsuarioActivoPuedeIniciarSesion() {
        User user = new User(1L, "carlos", "carlos@conicream.com", "hash", Set.of(Role.COMERCIANTE));

        assertTrue(user.isActivo());
        assertTrue(user.puedeIniciarSesion());
    }

    @Test
    @DisplayName("Debe bloquear el inicio de sesión cuando el usuario es suspendido")
    void testSuspenderUsuarioImpideInicioDeSesion() {
        User user = new User(1L, "pedro", "pedro@conicream.com", "hash", Set.of(Role.COMERCIANTE));

        user.suspender();

        assertFalse(user.isActivo());
        assertFalse(user.puedeIniciarSesion());
    }

    @Test
    @DisplayName("Debe reactivar a un usuario previamente suspendido")
    void testReactivarUsuario() {
        User user = new User(1L, "maria", "maria@conicream.com", "hash", Set.of(Role.COMERCIANTE));
        user.suspender();
        assertFalse(user.puedeIniciarSesion());

        user.reactivar();

        assertTrue(user.isActivo());
        assertTrue(user.puedeIniciarSesion());
    }

    @Test
    @DisplayName("Debe gestionar roles correctamente")
    void testGestionDeRoles() {
        User user = new User(1L, "admin", "admin@conicream.com", "hash", Set.of(Role.COMERCIANTE));

        assertFalse(user.hasRole(Role.ADMINISTRADOR));

        user.addRole(Role.ADMINISTRADOR);

        assertTrue(user.hasRole(Role.ADMINISTRADOR));
        assertTrue(user.hasRole(Role.COMERCIANTE));
    }
}
