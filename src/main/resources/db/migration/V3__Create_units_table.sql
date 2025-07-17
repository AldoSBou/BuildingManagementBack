CREATE TABLE units (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       building_id BIGINT NOT NULL,
                       unit_number VARCHAR(50) NOT NULL,
                       unit_type ENUM('APARTMENT', 'PARKING', 'STORAGE', 'COMMERCIAL') NOT NULL,
                       area DECIMAL(8,2),
                       owner_id BIGINT,
                       tenant_id BIGINT,
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       UNIQUE KEY uk_units_building_number (building_id, unit_number),
                       INDEX idx_units_building (building_id),
                       INDEX idx_units_owner (owner_id),
                       INDEX idx_units_tenant (tenant_id),

                       FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE,
                       FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL,
                       FOREIGN KEY (tenant_id) REFERENCES users(id) ON DELETE SET NULL
);