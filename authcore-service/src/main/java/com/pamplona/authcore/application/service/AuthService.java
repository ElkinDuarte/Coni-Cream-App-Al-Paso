package com.pamplona.authcore.application.service;

import com.pamplona.authcore.application.exception.InvalidCredentialsException;
import com.pamplona.authcore.application.exception.UserAlreadyExistsException;
import com.pamplona.authcore.application.exception.UserNotFoundException;
import com.pamplona.authcore.application.exception.UserSuspendedException;
import com.pamplona.authcore.application.ports.in.AssignRoleUseCase;
import com.pamplona.authcore.application.ports.in.CambiarEstadoUsuarioUseCase;
import com.pamplona.authcore.application.ports.in.ListUsersUseCase;
import com.pamplona.authcore.application.ports.in.LoginUseCase;
import com.pamplona.authcore.application.ports.in.RegisterUserUseCase;
import com.pamplona.authcore.application.ports.out.PasswordHasherPort;
import com.pamplona.authcore.application.ports.out.TokenProviderPort;
import com.pamplona.authcore.application.ports.out.UserRepositoryPort;
import com.pamplona.authcore.domain.Role;
import com.pamplona.authcore.domain.User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio de aplicación: Orquesta la autenticación y el ciclo de vida de usuarios y comerciantes.
 */
@Service
public class AuthService implements RegisterUserUseCase, LoginUseCase, AssignRoleUseCase, ListUsersUseCase, CambiarEstadoUsuarioUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenProviderPort tokenProvider;

    public AuthService(UserRepositoryPort userRepository,
                       PasswordHasherPort passwordHasher,
                       TokenProviderPort tokenProvider) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Long register(RegisterUserCommand command) {
        // [FEAT-02] Validaciones de unicidad de negocio
        if (userRepository.existsByUsername(command.username())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya está registrado: " + command.username());
        }
        if (command.email() != null && userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException("El correo electrónico ya está registrado: " + command.email());
        }
        if (command.numeroDocumento() != null && userRepository.existsByNumeroDocumento(command.numeroDocumento())) {
            throw new UserAlreadyExistsException("El documento de identidad ya está registrado: " + command.numeroDocumento());
        }

        Set<Role> roles = new HashSet<>(Set.of(Role.COMERCIANTE));
        User user = new User(
                null,
                command.tipoDocumento(),
                command.numeroDocumento(),
                command.username(),
                command.email(),
                passwordHasher.hash(command.rawPassword()),
                command.nombre(),
                command.apellido(),
                command.telefono(),
                true, // Inicia activo por defecto
                roles
        );

        User saved = userRepository.save(user);
        return saved.getId();
    }

    @Override
    public String login(String identifier, String rawPassword) {
        User user = userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.puedeIniciarSesion()) {
            throw new UserSuspendedException(identifier);
        }

        if (!passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return tokenProvider.generateToken(user);
    }

    @Override
    public void cambiarEstado(Long id, boolean activo) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // [FEAT-02] Invocación de métodos del modelo rico de dominio
        if (activo) {
            user.reactivar();
        } else {
            user.suspender();
        }

        userRepository.save(user);
    }

    @Override
    public void assignRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.addRole(role);
        userRepository.save(user);
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }
}