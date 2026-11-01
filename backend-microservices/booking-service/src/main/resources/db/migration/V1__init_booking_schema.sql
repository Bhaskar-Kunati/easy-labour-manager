CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id VARCHAR(255) NOT NULL,
    service_id BIGINT NOT NULL,
    booking_type VARCHAR(20) NOT NULL,
    requested_worker_count INTEGER NOT NULL,
    assigned_worker_count INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    scheduled_start_time TIMESTAMP,
    estimated_duration_minutes INTEGER NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    address VARCHAR(500) NOT NULL,
    landmark VARCHAR(255),
    special_instructions TEXT,
    subtotal DECIMAL(19, 2) NOT NULL,
    platform_fee DECIMAL(19, 2) NOT NULL,
    tax DECIMAL(19, 2) NOT NULL,
    discount DECIMAL(19, 2),
    total_amount DECIMAL(19, 2) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE booking_workers (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    worker_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    assigned_at TIMESTAMP,
    accepted_at TIMESTAMP,
    on_the_way_at TIMESTAMP,
    arrival_at TIMESTAMP,
    service_started_at TIMESTAMP,
    service_completed_at TIMESTAMP,
    earnings DECIMAL(19, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE TABLE worker_locations (
    worker_id VARCHAR(255) PRIMARY KEY,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
