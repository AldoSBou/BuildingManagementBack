CREATE TABLE buildings (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           address VARCHAR(500) NOT NULL,
                           description TEXT,
                           total_units INTEGER DEFAULT 0,
                           admin_user_id BIGINT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                           INDEX idx_buildings_name (name),
                           INDEX idx_buildings_admin (admin_user_id)
);