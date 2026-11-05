package com.pamplona.authcore.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByUsername(String username);

    Optional<UserJpaEntity> findByEmail(String email);

    // [FEAT-01] Consulta dual en una sola pasada a la base de datos
    @Query("SELECT u FROM UserJpaEntity u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<UserJpaEntity> findByUsernameOrEmail(@Param("identifier") String identifier);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByNumeroDocumento(String numeroDocumento);
}