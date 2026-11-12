CREATE TABLE booking_outbox_events (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    worker_id VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    reference_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    next_retry_time TIMESTAMP NOT NULL,
    last_error VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_outbox_status_next_retry ON booking_outbox_events(status, next_retry_time);
CREATE INDEX idx_outbox_reference ON booking_outbox_events(reference_id);
