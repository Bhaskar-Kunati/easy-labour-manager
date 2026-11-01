CREATE TABLE worker_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    profile_image VARCHAR(255),
    experience_years INTEGER,
    rating DECIMAL(3, 2) DEFAULT 0.00,
    total_jobs INTEGER DEFAULT 0,
    availability_status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    working_radius INTEGER DEFAULT 10,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_worker_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE worker_skills (
    id BIGSERIAL PRIMARY KEY,
    worker_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    experience_years INTEGER,
    hourly_rate DECIMAL(19, 2),
    daily_rate DECIMAL(19, 2),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_worker_profile FOREIGN KEY (worker_id) REFERENCES worker_profiles(id)
);

CREATE TABLE worker_documents (
    id BIGSERIAL PRIMARY KEY,
    worker_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_url VARCHAR(255) NOT NULL,
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_worker_doc FOREIGN KEY (worker_id) REFERENCES worker_profiles(id)
);