package com.pamplona.authcore.infrastructure.adapters.out.persistence;

import com.pamplona.authcore.application.ports.out.UserRepositoryPort;
import com.pamplona.authcore.domain.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida: implementa UserRepositoryPort usando Spring Data JPA.
 * Traduce de forma limpia entre User (Dominio) y UserJpaEntity (Infraestructura).
 */
@Component
public class UserRepositoryJpaAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryJpaAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = toEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String identifier) {
        return jpaRepository.findByUsernameOrEmail(identifier).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNumeroDocumento(String numeroDocumento) {
        return jpaRepository.existsByNumeroDocumento(numeroDocumento);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    // --- Métodos de Mapeo (Domain <-> JPA Entity) ---

    private User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                entity.getTipoDocumento(),
                entity.getNumeroDocumento(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getTelefono(),
                entity.isActivo(),
                new HashSet<>(entity.getRoles())
        );
    }

    private UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.getId(),
                user.getTipoDocumento(),
                user.getNumeroDocumento(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getNombre(),
                user.getApellido(),
                user.getTelefono(),
                user.isActivo(),
                new HashSet<>(user.getRoles())
        );
    }
}
