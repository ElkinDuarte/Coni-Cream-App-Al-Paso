package com.pamplona.authcore.infrastructure.adapters.in.rest;

import com.pamplona.authcore.application.ports.in.AssignRoleUseCase;
import com.pamplona.authcore.application.ports.in.CambiarEstadoUsuarioUseCase;
import com.pamplona.authcore.application.ports.in.ListUsersUseCase;
import com.pamplona.authcore.application.ports.in.LoginUseCase;
import com.pamplona.authcore.application.ports.in.RegisterUserUseCase;
import com.pamplona.authcore.infrastructure.adapters.in.rest.dto.AssignRoleRequest;
import com.pamplona.authcore.infrastructure.adapters.in.rest.dto.CambiarEstadoRequest;
import com.pamplona.authcore.infrastructure.adapters.in.rest.dto.LoginRequest;
import com.pamplona.authcore.infrastructure.adapters.in.rest.dto.LoginResponse;
import com.pamplona.authcore.infrastructure.adapters.in.rest.dto.RegisterRequest;
import com.pamplona.authcore.infrastructure.adapters.in.rest.dto.RegisterResponse;
import com.pamplona.authcore.infrastructure.adapters.in.rest.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/auth", "/api/auth"})
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final AssignRoleUseCase assignRoleUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          AssignRoleUseCase assignRoleUseCase,
                          ListUsersUseCase listUsersUseCase,
                          CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.assignRoleUseCase = assignRoleUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.cambiarEstadoUsuarioUseCase = cambiarEstadoUsuarioUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        // [FEAT-02] Mapeo al Command inmutable de la capa de aplicación
        RegisterUserUseCase.RegisterUserCommand command = new RegisterUserUseCase.RegisterUserCommand(
                request.tipoDocumento(),
                request.numeroDocumento(),
                request.username(),
                request.email(),
                request.password(),
                request.nombre(),
                request.apellido(),
                request.telefono()
        );
        Long id = registerUserUseCase.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(id, request.username()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = loginUseCase.login(request.identifier(), request.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    // [FEAT-02] Endpoint administrativo para suspender o reactivar comerciantes
    @PatchMapping("/users/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN')")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {
        cambiarEstadoUsuarioUseCase.cambiarEstado(id, request.activo());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/roles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN')")
    public ResponseEntity<Void> assignRole(@PathVariable Long id, @Valid @RequestBody AssignRoleRequest request) {
        assignRoleUseCase.assignRole(id, request.role());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN')")
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<UserResponse> users = listUsersUseCase.listUsers().stream().map(UserResponse::from).toList();
        return ResponseEntity.ok(users);
    }
}