CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          fee_id BIGINT NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          payment_date DATE NOT NULL,
                          payment_method VARCHAR(50) DEFAULT 'CASH',
                          receipt_number VARCHAR(100),
                          notes TEXT,
                          recorded_by BIGINT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          INDEX idx_payments_fee (fee_id),
                          INDEX idx_payments_date (payment_date),
                          INDEX idx_payments_receipt (receipt_number),
                          INDEX idx_payments_recorded_by (recorded_by),

                          FOREIGN KEY (fee_id) REFERENCES fees(id) ON DELETE CASCADE,
                          FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE SET NULL
);