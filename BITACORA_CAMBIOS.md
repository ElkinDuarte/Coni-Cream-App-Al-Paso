# Bitácora de Decisiones Arquitectónicas y Refactorización (AuthCore Service)

### [2026-11-04] - Migración de Motor de Persistencia de MySQL a PostgreSQL
* **Capa arquitectónica:** Infraestructura (`build.gradle` y `application.yml`).
* **¿Qué se cambió o agregó?**
  - Se sustituyó el driver `mysql-connector-j` por el driver oficial `org.postgresql:postgresql`.
  - Se actualizó `application.yml` modificando el dialecto a `PostgreSQLDialect`, el driver a `org.postgresql.Driver` y la URL local por defecto a `jdbc:postgresql://localhost:5432/auth_db`.
* **¿Por qué se hizo?**
  - Para unificar el estándar de persistencia de todo el sistema en PostgreSQL.
  - Para desacoplar el servicio de una instancia de pruebas externa de AWS RDS que venía hardcodeada con credenciales expuestas en el repositorio.
* **Impacto arquitectónico:** Se mantiene la interfaz del adaptador de persistencia intacta (`UserRepositoryJpaAdapter`), demostrando el principio de Arquitectura Hexagonal: cambiar la tecnología de infraestructura (el motor SQL) no requirió tocar una sola línea de código en el Dominio ni en la Aplicación.

### [2026-11-04] - Actualización del Stack Tecnológico (Java 25 + Spring Boot 4.1.2)
* **Capa arquitectónica:** Infraestructura (`build.gradle`).
* **¿Qué se cambió o agregó?**
  - Se actualizó el plugin de Spring Boot a la versión estable `4.1.2`.
  - Se configuró el toolchain de Gradle para compilar y ejecutar bajo `JavaLanguageVersion.of(25)`.
* **¿Por qué se hizo?**
  - Para alinear el microservicio con el diseño arquitectónico original proyectado en Java 25.
  - Para aprovechar las optimizaciones de Virtual Threads, mejoras de concurrencia y los últimos parches de seguridad de la rama Spring Boot 4.1.
* **Impacto arquitectónico:** Cero impacto en el dominio. El núcleo de la aplicación sigue siendo Java puro desacoplado, beneficiándose de la optimización del runtime de Java 25 y Spring Boot 4 sin alterar contratos ni casos de uso.

### [2026-11-04] - Evolución del Modelo de Dominio (User y Roles para FEAT-01 y FEAT-02)
* **Capa arquitectónica:** Dominio (`domain/User.java`, `domain/Role.java` y nuevo `domain/TipoDocumento.java`).
* **¿Qué se cambió o agregó?**
  - Se sustituyó el enum de roles (`ADMIN`, `USER`) por la terminología de negocio (`ADMINISTRADOR`, `COMERCIANTE`).
  - Se creó el enum `TipoDocumento` (`CC`, `CE`, `PPT`, `PAS`).
  - Se agregaron a `User` los atributos `email`, `tipoDocumento`, `numeroDocumento`, `nombre`, `apellido`, `telefono` y `activo`.
  - Se introdujeron métodos de negocio DDD (`suspender()`, `reactivar()`, `puedeIniciarSesion()`).
* **¿Por qué se hizo?**
  - Para soportar el Login Dual (usuario o correo) y la validación de cuenta activa (`FEAT-01`).
  - Para dar soporte al ciclo de vida y suspensión de comerciantes con identificación binacional (`FEAT-02`).
* **Impacto arquitectónico:** Se robustece el núcleo del dominio sin introducir ninguna dependencia externa ni librerías de framework (cero Lombok, cero Spring).

### [2026-11-04] - Implementación de Login Dual y Bloqueo de Cuentas Suspendidas (FEAT-01)
* **Capa arquitectónica:** Aplicación (`application/ports/`, `application/service/` y `application/exception/`).
* **¿Qué se cambió o agregó?**
  - Se creó la excepción de negocio `UserSuspendedException`.
  - Se amplió el puerto `UserRepositoryPort` con métodos de consulta dual (`findByUsernameOrEmail`) y validaciones de unicidad.
  - Se modificó `LoginUseCase` y `AuthService.login()` para admitir inicio de sesión dual (nombre de usuario o correo electrónico).
  - Se incorporó la regla de negocio que rechaza intentos de autenticación de usuarios inactivos (`user.puedeIniciarSesion()`).
  - Se fijó el rol por defecto `COMERCIANTE` en el método de registro de la aplicación.
* **¿Por qué se hizo?**
  - Para cumplir con el requerimiento funcional `[FEAT-01]` de login unificado y control de acceso por estado de cuenta.
* **Impacto arquitectónico:** Los casos de uso orquestan las invariantes del dominio apoyándose en los puertos de salida, manteniendo la lógica desacoplada de la tecnología de persistencia.

### [2026-11-04] - Actualización de la Persistencia JPA (Adaptador y Entidad para FEAT-01 y FEAT-02)
* **Capa arquitectónica:** Infraestructura (`infrastructure/adapters/out/persistence/`).
* **¿Qué se cambió o agregó?**
  - Se extendió `UserJpaEntity` con los campos `email`, `tipoDocumento`, `numeroDocumento`, `nombre`, `apellido`, `telefono` y `activo`.
  - Se añadió la consulta JPQL `@Query` personalizada en `UserJpaRepository` para búsqueda atómica por usuario o correo (`findByUsernameOrEmail`).
  - Se implementaron los métodos de existencia (`existsByEmail`, `existsByNumeroDocumento`) y borrado en `UserRepositoryJpaAdapter`.
  - Se actualizó el mapeador bidireccional (`toDomain` y `toEntity`) en el adaptador.
* **¿Por qué se hizo?**
  - Para soportar la persistencia física de los nuevos atributos del modelo de dominio y dar soporte en base de datos al Login Dual.
* **Impacto arquitectónico:** Se preserva la regla de oro hexagonal: `User` (dominio) nunca ve las anotaciones JPA; toda la complejidad de base de datos queda encapsulada exclusivamente en el adaptador de infraestructura.

### [2026-11-04] - Actualización de la Capa REST y Control de Excepciones (FEAT-01)
* **Capa arquitectónica:** Infraestructura Adaptador de Entrada (`infrastructure/adapters/in/rest/`).
* **¿Qué se cambió o agregó?**
  - Se versionó el controlador `AuthController` exponiendo `/api/v1/auth` junto con retrocompatibilidad.
  - Se configuró `LoginRequest` con `@JsonAlias` para admitir `username`, `email` o `identifier`.
  - Se actualizó `GlobalExceptionHandler` con el método `handleUserSuspended` retornando código HTTP 403 Forbidden.
  - Se actualizaron las anotaciones `@PreAuthorize` a `hasAnyRole('ADMINISTRADOR', 'ADMIN')`.
* **¿Por qué se hizo?**
  - Para finalizar la integración del flujo de Login Dual y rechazar con semántica HTTP adecuada las cuentas bloqueadas.
* **Impacto arquitectónico:** Los adaptadores HTTP traducen las peticiones y excepciones de negocio sin que el dominio ni la aplicación conozcan de códigos HTTP ni anotaciones de Jackson.

### [2026-11-04] - Actualización de AdminBootstrapRunner a Roles de Dominio Coni-Cream
* **Capa arquitectónica:** Infraestructura (`infrastructure/config/AdminBootstrapRunner.java`).
* **¿Qué se cambió o agregó?**
  - Se actualizó la asignación del rol inicial a `Role.ADMINISTRADOR`.
  - Se configuró el correo inicial `admin@conicream.com` para habilitar el Login Dual en el usuario maestro.
* **¿Por qué se hizo?**
  - Para sincronizar el arranque automático con la nueva firma de `User` y los roles del negocio.
* **Impacto arquitectónico:** Permite el inicio autónomo del microservicio y la creación de credenciales iniciales respetando las invariantes del dominio.

### [2026-11-04] - Configuración de Contenedores con PostgreSQL y Semillero Efímero
* **Capa arquitectónica:** Infraestructura DevOps (`docker-compose.yml`, `Dockerfile`, `init-db/`).
* **¿Qué se cambió o agregó?**
  - Se sustituyó la imagen de MySQL por `postgres:17-alpine` en `docker-compose.yml`.
  - Se configuró la ejecución automática del DDL (`01_schema.sql`) y seed de prueba (`02_seed.sql`) mediante el punto de entrada `/docker-entrypoint-initdb.d`.
  - Se eliminaron los volúmenes persistentes de base de datos para garantizar entornos de prueba efímeros y reproducibles.
  - Se integró `healthcheck` para sincronizar el arranque de Spring Boot una vez que la base de datos esté lista.
  - Se actualizó el `Dockerfile` multi-stage al runtime oficial de Java 25.
* **¿Por qué se hizo?**
  - Para permitir pruebas end-to-end completas y aisladas en contenedores sin requerir instalación local ni costos en la nube.
* **Impacto arquitectónico:** Permite desplegar y destruir el microservicio de autenticación de forma determinística y con un solo comando.

### [2026-11-04] - Actualización de Rutas Públicas en SecurityConfig
* **Capa arquitectónica:** Infraestructura Configuración (`infrastructure/config/SecurityConfig.java`).
* **¿Qué se cambió o agregó?**
  - Se habilitaron las rutas versionadas `/api/v1/auth/login` y `/api/v1/auth/register` en la lista pública (`permitAll()`) de la cadena de filtros de Spring Security.
* **¿Por qué se hizo?**
  - Para permitir el acceso público a las rutas oficiales de la API REST sin ser interceptadas por el firewall de seguridad previo a la autenticación.
* **Impacto arquitectónico:** Mantiene la seguridad declarativa de Spring Security alineada con el contrato REST de entrada.
