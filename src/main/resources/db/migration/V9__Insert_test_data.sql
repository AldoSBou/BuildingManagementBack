-- Ejemplo de aplicación principal con configuración Bean

-- Users table foreign keys
ALTER TABLE users
    ADD CONSTRAINT fk_users_building
        FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE SET NULL;

-- Buildings table foreign keys
ALTER TABLE buildings
    ADD CONSTRAINT fk_buildings_admin
        FOREIGN KEY (admin_user_id) REFERENCES users(id) ON DELETE SET NULL;

-- Units table foreign keys
ALTER TABLE units
    ADD CONSTRAINT fk_units_building
        FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE;

ALTER TABLE units
    ADD CONSTRAINT fk_units_owner
        FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE units
    ADD CONSTRAINT fk_units_tenant
        FOREIGN KEY (tenant_id) REFERENCES users(id) ON DELETE SET NULL;

-- Fee types table foreign keys
ALTER TABLE fee_types
    ADD CONSTRAINT fk_fee_types_building
        FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE;

-- Fees table foreign keys
ALTER TABLE fees
    ADD CONSTRAINT fk_fees_unit
        FOREIGN KEY (unit_id) REFERENCES units(id) ON DELETE CASCADE;

ALTER TABLE fees
    ADD CONSTRAINT fk_fees_type
        FOREIGN KEY (fee_type_id) REFERENCES fee_types(id) ON DELETE CASCADE;

-- Payments table foreign keys
ALTER TABLE payments
    ADD CONSTRAINT fk_payments_fee
        FOREIGN KEY (fee_id) REFERENCES fees(id) ON DELETE CASCADE;

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_recorded_by
        FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE SET NULL;

-- Expenses table foreign keys
ALTER TABLE expenses
    ADD CONSTRAINT fk_expenses_building
        FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE;

ALTER TABLE expenses
    ADD CONSTRAINT fk_expenses_approved_by
        FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE expenses
    ADD CONSTRAINT fk_expenses_recorded_by
        FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE SET NULL;

-- Announcements table foreign keys
ALTER TABLE announcements
    ADD CONSTRAINT fk_announcements_building
        FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE;

ALTER TABLE announcements
    ADD CONSTRAINT fk_announcements_published_by
        FOREIGN KEY (published_by) REFERENCES users(id) ON DELETE SET NULL;

-- src/main/resources/db/migration/V10__Insert_test_data.sql
-- Insertar datos de prueba

-- Insertar usuarios de prueba (password: 123456)
INSERT IGNORE INTO users (email, password, name, phone, role, building_id, is_active)
VALUES
('admin@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P3nwf9UToz9bJK', 'Admin User', '999999999', 'ADMIN', NULL, true),
('board@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P3nwf9UToz9bJK', 'Board Member', '888888888', 'BOARD_MEMBER', NULL, true),
('owner@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P3nwf9UToz9bJK', 'Owner User', '777777777', 'OWNER', NULL, true),
('tenant@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P3nwf9UToz9bJK', 'Tenant User', '666666666', 'TENANT', NULL, true);

-- Insertar edificio de prueba
INSERT IGNORE INTO buildings (name, address, description, total_units, admin_user_id)
VALUES
('Edificio San Martín', 'Av. San Martín 123, San Isidro, Lima', 'Edificio residencial moderno con 20 departamentos', 20, 1);

-- Actualizar building_id en usuarios
UPDATE users
SET building_id = 1
WHERE email IN ('board@test.com', 'owner@test.com', 'tenant@test.com')
  AND building_id IS NULL;

-- Insertar unidades de prueba
INSERT IGNORE INTO units (building_id, unit_number, unit_type, area, owner_id, is_active)
VALUES
(1, '101', 'APARTMENT', 85.50, (SELECT id FROM users WHERE email = 'owner@test.com'), true),
(1, '102', 'APARTMENT', 92.00, (SELECT id FROM users WHERE email = 'owner@test.com'), true),
(1, '201', 'APARTMENT', 85.50, NULL, true),
(1, 'P01', 'PARKING', 12.00, (SELECT id FROM users WHERE email = 'owner@test.com'), true),
(1, 'P02', 'PARKING', 12.00, NULL, true),
(1, 'D01', 'STORAGE', 6.00, (SELECT id FROM users WHERE email = 'owner@test.com'), true);

-- Insertar tipos de cuotas
INSERT IGNORE INTO fee_types (building_id, name, description, base_amount, frequency, is_per_area, is_active)
VALUES
(1, 'Mantenimiento', 'Cuota mensual de mantenimiento', 150.00, 'MONTHLY', false, true),
(1, 'Fondo de Reserva', 'Fondo para reparaciones mayores', 50.00, 'MONTHLY', false, true),
(1, 'Agua', 'Consumo de agua común', 30.00, 'MONTHLY', false, true),
(1, 'Seguridad', 'Servicio de seguridad 24/7', 100.00, 'MONTHLY', false, true);

-- Insertar cuotas de enero 2025
INSERT IGNORE INTO fees (unit_id, fee_type_id, amount, due_date, status, period_month, period_year)
VALUES
(1, 1, 150.00, '2025-01-15', 'PENDING', 1, 2025),
(1, 2, 50.00, '2025-01-15', 'PENDING', 1, 2025),
(1, 3, 30.00, '2025-01-15', 'PENDING', 1, 2025),
(1, 4, 100.00, '2025-01-15', 'PENDING', 1, 2025),
(2, 1, 150.00, '2025-01-15', 'PAID', 1, 2025),
(2, 2, 50.00, '2025-01-15', 'PAID', 1, 2025);

-- Insertar algunos pagos
INSERT IGNORE INTO payments (fee_id, amount, payment_date, payment_method, receipt_number, recorded_by)
VALUES
(5, 150.00, '2025-01-10', 'TRANSFER', 'REC-001', 2),
(6, 50.00, '2025-01-10', 'TRANSFER', 'REC-002', 2);

-- Insertar algunos gastos
INSERT IGNORE INTO expenses (building_id, category, amount, description, expense_date, recorded_by)
VALUES
(1, 'CLEANING', 800.00, 'Servicio de limpieza mensual', '2025-01-05', 2),
(1, 'SECURITY', 2500.00, 'Pago mensual empresa de seguridad', '2025-01-01', 2),
(1, 'UTILITIES', 450.00, 'Recibo de luz áreas comunes', '2025-01-08', 2),
(1, 'MAINTENANCE', 320.00, 'Reparación de bomba de agua', '2025-01-12', 2);

-- Insertar anuncios
INSERT IGNORE INTO announcements (building_id, title, content, is_important, published_by)
VALUES
(1, 'Bienvenidos al Sistema', 'Estimados propietarios, ya pueden acceder al nuevo sistema de gestión del edificio para revisar sus estados de cuenta y pagos.', true, 2),
(1, 'Mantenimiento Programado', 'El día sábado 25 de enero se realizará mantenimiento de la bomba de agua desde las 8:00 AM hasta las 12:00 PM.', false, 2),
(1, 'Asamblea General', 'Se convoca a asamblea general para el día 30 de enero a las 7:00 PM en el salón comunal.', true, 2);