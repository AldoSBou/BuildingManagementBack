CREATE TABLE expenses (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          building_id BIGINT NOT NULL,
                          category ENUM('CLEANING', 'SECURITY', 'UTILITIES', 'MAINTENANCE', 'ADMINISTRATION', 'OTHER') NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          description TEXT NOT NULL,
                          expense_date DATE NOT NULL,
                          receipt_url VARCHAR(500),
                          approved_by BIGINT,
                          recorded_by BIGINT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          INDEX idx_expenses_building (building_id),
                          INDEX idx_expenses_category (category),
                          INDEX idx_expenses_date (expense_date),
                          INDEX idx_expenses_approved_by (approved_by),

                          FOREIGN KEY (building_id) REFERENCES buildings(id) ON DELETE CASCADE,
                          FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
                          FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE SET NULL
);