CREATE TABLE fees (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      unit_id BIGINT NOT NULL,
                      fee_type_id BIGINT NOT NULL,
                      amount DECIMAL(10,2) NOT NULL,
                      due_date DATE NOT NULL,
                      status ENUM('PENDING', 'PAID', 'OVERDUE', 'PARTIAL', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
                      period_month INTEGER NOT NULL,
                      period_year INTEGER NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                      UNIQUE KEY uk_fees_unit_type_period (unit_id, fee_type_id, period_month, period_year),
                      INDEX idx_fees_unit (unit_id),
                      INDEX idx_fees_type (fee_type_id),
                      INDEX idx_fees_status (status),
                      INDEX idx_fees_due_date (due_date),
                      INDEX idx_fees_period (period_year, period_month),

                      FOREIGN KEY (unit_id) REFERENCES units(id) ON DELETE CASCADE,
                      FOREIGN KEY (fee_type_id) REFERENCES fee_types(id) ON DELETE CASCADE
);