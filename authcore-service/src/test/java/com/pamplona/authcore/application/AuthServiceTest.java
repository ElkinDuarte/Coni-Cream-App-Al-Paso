package com.pamplona.authcore.application;

import com.pamplona.authcore.application.exception.InvalidCredentialsException;
import com.pamplona.authcore.application.exception.UserAlreadyExistsException;
import com.pamplona.authcore.application.exception.UserSuspendedException;
import com.pamplona.authcore.application.ports.in.RegisterUserUseCase.RegisterUserCommand;
import com.pamplona.authcore.application.ports.out.PasswordHasherPort;
import com.pamplona.authcore.application.ports.out.TokenProviderPort;
import com.pamplona.authcore.application.ports.out.UserRepositoryPort;
import com.pamplona.authcore.application.service.AuthService;
import com.pamplona.authcore.domain.Role;
import com.pamplona.authcore.domain.TipoDocumento;
import com.pamplona.authcore.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de Aplicación: AuthService")
class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private PasswordHasherPort passwordHasher;
    @Mock
    private TokenProviderPort tokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordHasher, tokenProvider);
    }

    @Test
    @DisplayName("[FEAT-01] Login exitoso usando username")
    void testLoginExitosoPorUsername() {
        User user = new User(1L, "carlos", "carlos@conicream.com", "hash123", Set.of(Role.COMERCIANTE));
        when(userRepository.findByUsernameOrEmail("carlos")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("1234", "hash123")).thenReturn(true);
        when(tokenProvider.generateToken(user)).thenReturn("jwt.token.valido");

        String token = authService.login("carlos", "1234");

        assertNotNull(token);
        assertEquals("jwt.token.valido", token);
        verify(tokenProvider).generateToken(user);
    }

    @Test
    @DisplayName("[FEAT-01] Login exitoso dual usando email")
    void testLoginExitosoPorEmail() {
        User user = new User(1L, "carlos", "carlos@conicream.com", "hash123", Set.of(Role.COMERCIANTE));
        when(userRepository.findByUsernameOrEmail("carlos@conicream.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("1234", "hash123")).thenReturn(true);
        when(tokenProvider.generateToken(user)).thenReturn("jwt.token.email");

        String token = authService.login("carlos@conicream.com", "1234");

        assertNotNull(token);
        assertEquals("jwt.token.email", token);
    }

    @Test
    @DisplayName("[FEAT-01 y FEAT-02] Login rechazado cuando la cuenta está suspendida")
    void testLoginFallaPorCuentaSuspendida() {
        User user = new User(2L, "pedro", "pedro@conicream.com", "hash123", Set.of(Role.COMERCIANTE));
        user.suspender(); // activo = false
        when(userRepository.findByUsernameOrEmail("pedro")).thenReturn(Optional.of(user));

        assertThrows(UserSuspendedException.class, () -> authService.login("pedro", "1234"));
        verify(passwordHasher, never()).matches(any(), any());
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("[FEAT-01] Login rechazado por contraseña errónea")
    void testLoginFallaPorPasswordInvalido() {
        User user = new User(1L, "carlos", "carlos@conicream.com", "hash123", Set.of(Role.COMERCIANTE));
        when(userRepository.findByUsernameOrEmail("carlos")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrong_pass", "hash123")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login("carlos", "wrong_pass"));
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("[FEAT-02] Registro de comerciante exitoso")
    void testRegistroComercianteExitoso() {
        RegisterUserCommand command = new RegisterUserCommand(
                TipoDocumento.CC, "1090123", "nuevo", "nuevo@conicream.com",
                "pass123", "Nuevo", "Usuario", "300123"
        );

        when(userRepository.existsByUsername("nuevo")).thenReturn(false);
        when(userRepository.existsByEmail("nuevo@conicream.com")).thenReturn(false);
        when(userRepository.existsByNumeroDocumento("1090123")).thenReturn(false);
        when(passwordHasher.hash("pass123")).thenReturn("hashedPass");

        User savedUser = new User(10L, TipoDocumento.CC, "1090123", "nuevo", "nuevo@conicream.com",
                "hashedPass", "Nuevo", "Usuario", "300123", true, Set.of(Role.COMERCIANTE));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Long id = authService.register(command);

        assertEquals(10L, id);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("[FEAT-02] Registro rechazado por documento duplicado")
    void testRegistroFallaPorDocumentoDuplicado() {
        RegisterUserCommand command = new RegisterUserCommand(
                TipoDocumento.CC, "1090123", "nuevo", "nuevo@conicream.com",
                "pass123", "Nuevo", "Usuario", "300123"
        );

        when(userRepository.existsByUsername("nuevo")).thenReturn(false);
        when(userRepository.existsByEmail("nuevo@conicream.com")).thenReturn(false);
        when(userRepository.existsByNumeroDocumento("1090123")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(command));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("[FEAT-02] Cambiar estado a suspendido exitosamente")
    void testCambiarEstadoSuspender() {
        User user = new User(1L, "carlos", "carlos@conicream.com", "hash", Set.of(Role.COMERCIANTE));
        assertTrue(user.isActivo());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        authService.cambiarEstado(1L, false);

        assertFalse(user.isActivo());
        verify(userRepository).save(user);
    }
}
