package com.pamplona.authcore.infrastructure.config;

import com.pamplona.authcore.application.ports.out.PasswordHasherPort;
import com.pamplona.authcore.application.ports.out.UserRepositoryPort;
import com.pamplona.authcore.domain.Role;
import com.pamplona.authcore.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Crea el usuario administrador inicial al arrancar si la base de datos está vacía.
 */
@Component
public class AdminBootstrapRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@conicream.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;

    public AdminBootstrapRunner(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(DEFAULT_ADMIN_USERNAME).isEmpty()) {
            Set<Role> roles = new HashSet<>(Set.of(Role.ADMINISTRADOR));
            // Uso del constructor con soporte para login dual por email
            User admin = new User(
                    null,
                    DEFAULT_ADMIN_USERNAME,
                    DEFAULT_ADMIN_EMAIL,
                    passwordHasher.hash(DEFAULT_ADMIN_PASSWORD),
                    roles
            );
            userRepository.save(admin);
            log.warn("=================================================================");
            log.warn(" Usuario administrador inicial creado con éxito:");
            log.warn("   username: {}", DEFAULT_ADMIN_USERNAME);
            log.warn("   email:    {}", DEFAULT_ADMIN_EMAIL);
            log.warn("   password: {}", DEFAULT_ADMIN_PASSWORD);
            log.warn("   rol:      {}", Role.ADMINISTRADOR);
            log.warn("=================================================================");
        }
    }
}