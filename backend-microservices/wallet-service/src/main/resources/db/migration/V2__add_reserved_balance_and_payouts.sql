-- V2__add_reserved_balance_and_payouts.sql
-- Add reserved_balance to wallets table
ALTER TABLE wallets ADD COLUMN IF NOT EXISTS reserved_balance DECIMAL(19,2) NOT NULL DEFAULT 0.00;

-- Create payout_requests table
CREATE TABLE IF NOT EXISTS payout_requests (
    id BIGSERIAL PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    destination_id VARCHAR(100) NOT NULL,
    destination_type VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    reference_id VARCHAR(100) NOT NULL UNIQUE,
    gateway_reference VARCHAR(100),
    failure_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_payout_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);

CREATE INDEX IF NOT EXISTS idx_payout_user_id ON payout_requests(user_id);
CREATE INDEX IF NOT EXISTS idx_payout_status ON payout_requests(status);
