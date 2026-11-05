package com.pamplona.authcore.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad de dominio pura: Representa tanto a Administradores como a Comerciantes.
 * Cero dependencias de frameworks ni librerías de infraestructura.
 */
public class User {

    private Long id;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String username;
    private String email;
    private String passwordHash;
    private String nombre;
    private String apellido;
    private String telefono;
    private boolean activo;
    private Set<Role> roles;

    // Constructor completo
    public User(Long id, TipoDocumento tipoDocumento, String numeroDocumento,
                String username, String email, String passwordHash,
                String nombre, String apellido, String telefono,
                boolean activo, Set<Role> roles) {
        this.id = id;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.activo = activo;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
    }

    // Constructor de conveniencia (para bootstrapping o registros rápidos con estado activo)
    public User(Long id, String username, String email, String passwordHash, Set<Role> roles) {
        this(id, null, null, username, email, passwordHash, null, null, null, true, roles);
    }

    // --- Métodos de Negocio (DDD) ---

    public void suspender() {
        this.activo = false;
    }

    public void reactivar() {
        this.activo = true;
    }

    public boolean puedeIniciarSesion() {
        return this.activo;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    // --- Getters y Setters explícitos (Sin Lombok) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Set<Role> getRoles() {
        return new HashSet<>(roles);
    }
}
