-- ============================================================================
-- 02_seed.sql - Datos de prueba iniciales para Coni-Cream
-- Contraseña para todos los usuarios de prueba: password123
-- ============================================================================

-- 1. Comerciante Activo (Listo para operar en ruta)
INSERT INTO users (tipo_documento, numero_documento, username, email, password_hash, nombre, apellido, telefono, activo)
VALUES (
    'CC',
    '1090123456',
    'carlos_comerciante',
    'carlos@conicream.com',
    '$2a$12$24wFePXEtESz1ZNj4GFgw.wwUILZYloHerT3LKRahdeT0LKx38./i',
    'Carlos',
    'Pérez',
    '3101234567',
    TRUE
);

-- 2. Comerciante Suspendido (Para probar rechazo de login y bloqueo)
INSERT INTO users (tipo_documento, numero_documento, username, email, password_hash, nombre, apellido, telefono, activo)
VALUES (
    'PPT',
    '987654321',
    'pedro_suspendido',
    'pedro@conicream.com',
    '$2a$12$24wFePXEtESz1ZNj4GFgw.wwUILZYloHerT3LKRahdeT0LKx38./i',
    'Pedro',
    'González',
    '3159876543',
    FALSE
);

-- Asignación de Roles
INSERT INTO user_roles (user_id, role)
SELECT id, 'COMERCIANTE' FROM users WHERE username IN ('carlos_comerciante', 'pedro_suspendido');
