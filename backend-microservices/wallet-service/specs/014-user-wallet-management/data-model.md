# Data Model: User Wallet & Transaction Management

## Entities

### 1. Wallet (`wallets`)
Represents an individual user's digital wallet account.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `BIGINT` | Primary Key, Auto-increment | Unique wallet identifier |
| `user_id` | `VARCHAR(100)` | Not Null, Unique | Associated user identifier from Auth/User service |
| `balance` | `DECIMAL(19,2)` | Not Null, Default `0.00` | Current available balance |
| `currency` | `VARCHAR(3)` | Not Null, Default `'INR'` | Currency code (ISO 4217) |
| `status` | `VARCHAR(20)` | Not Null, Default `'ACTIVE'` | Wallet status (`ACTIVE`, `SUSPENDED`) |
| `created_at` | `TIMESTAMP` | Not Null | Creation timestamp |
| `updated_at` | `TIMESTAMP` | Not Null | Last update timestamp |

### 2. Wallet Transaction (`wallet_transactions`)
Represents an immutable financial ledger entry (credit or debit).

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `BIGINT` | Primary Key, Auto-increment | Unique transaction identifier |
| `wallet_id` | `BIGINT` | Foreign Key (`wallets.id`), Not Null | Associated wallet ID |
| `amount` | `DECIMAL(19,2)` | Not Null, Positive | Transaction amount |
| `type` | `VARCHAR(20)` | Not Null | Transaction type (`CREDIT`, `DEBIT`) |
| `reference_id` | `VARCHAR(100)` | Unique (nullable for manual admin ops) | Idempotency reference (e.g. booking ID, payment ID) |
| `description` | `VARCHAR(255)` | Nullable | Human-readable transaction description |
| `created_at` | `TIMESTAMP` | Not Null | Transaction timestamp |

## Indexes & Constraints
- Unique index on `wallets(user_id)` to enforce a 1-to-1 relationship between user and wallet.
- Unique index on `wallet_transactions(reference_id)` where `reference_id IS NOT NULL` to guarantee idempotency.
- Index on `wallet_transactions(wallet_id)` for efficient ledger queries.
