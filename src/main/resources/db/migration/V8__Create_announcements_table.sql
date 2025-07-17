CREATE TABLE announcements (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               building_id BIGINT NOT NULL,
                               title VARCHAR(255) NOT NULL,
                               content TEXT NOT NULL,
                               is_important BOOLEAN DEFAULT FALSE,
                               published_by BIGINT,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                               INDEX idx_announcements_building (building_id),
                               INDEX idx_announcements_important (is_important),
                               INDEX idx_announcements_date (created_at),

                               FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE,
                               FOREIGN KEY (published_by) REFERENCES users(id) ON DELETE SET NULL
);