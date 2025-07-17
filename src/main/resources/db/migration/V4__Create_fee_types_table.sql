CREATE TABLE fee_types (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           building_id BIGINT NOT NULL,
                           name VARCHAR(255) NOT NULL,
                           description TEXT,
                           base_amount DECIMAL(10,2) NOT NULL,
                           frequency ENUM('MONTHLY', 'QUARTERLY', 'ANNUAL', 'ONE_TIME') NOT NULL DEFAULT 'MONTHLY',
                           is_per_area BOOLEAN DEFAULT FALSE,
                           is_active BOOLEAN DEFAULT TRUE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                           INDEX idx_fee_types_building (building_id),
                           INDEX idx_fee_types_active (is_active),

                           FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE
);