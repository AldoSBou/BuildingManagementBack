CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       role ENUM('ADMIN', 'BOARD_MEMBER', 'OWNER', 'TENANT') NOT NULL,
                       building_id BIGINT,
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       INDEX idx_users_email (email),
                       INDEX idx_users_building_id (building_id),
                       INDEX idx_users_role (role)
);