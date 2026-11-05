package com.pamplona.authcore.application.ports.out;

import com.pamplona.authcore.domain.User;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: persistencia de usuarios.
 * El dominio y la aplicación solo conocen esta interfaz.
 */
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String identifier);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByNumeroDocumento(String numeroDocumento);
    List<User> findAll();
    void deleteById(Long id);
}