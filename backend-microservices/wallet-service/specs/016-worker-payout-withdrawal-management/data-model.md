# Data Model: Worker Payout & Bank Withdrawal Management

## Entities

### 1. Wallet Updates (`wallets` table modification)
Adds `reserved_balance` to track funds currently locked in pending payouts.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `BIGINT` | Primary Key, Auto-increment | Unique wallet identifier |
| `user_id` | `VARCHAR(100)` | Not Null, Unique | Associated user identifier |
| `balance` | `DECIMAL(19,2)` | Not Null, Default `0.00` | Current available balance |
| `reserved_balance` | `DECIMAL(19,2)` | Not Null, Default `0.00` | Balance reserved for pending payouts |
| `currency` | `VARCHAR(3)` | Not Null, Default `'INR'` | Currency code |
| `status` | `VARCHAR(20)` | Not Null, Default `'ACTIVE'` | Wallet status |
| `version` | `BIGINT` | Optimistic locking | Version number |

### 2. Payout Request (`payout_requests` table)
Represents a worker's withdrawal request and its lifecycle state.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `BIGINT` | Primary Key, Auto-increment | Unique payout request ID |
| `wallet_id` | `BIGINT` | Foreign Key (`wallets.id`), Not Null | Associated wallet ID |
| `user_id` | `VARCHAR(100)` | Not Null | Worker user ID |
| `amount` | `DECIMAL(19,2)` | Not Null, Positive | Payout amount requested |
| `currency` | `VARCHAR(3)` | Not Null, Default `'INR'` | Currency code |
| `destination_id` | `VARCHAR(100)` | Not Null | Bank account or UPI destination ID |
| `destination_type` | `VARCHAR(20)` | Not Null | `BANK_ACCOUNT` or `UPI` |
| `status` | `VARCHAR(30)` | Not Null, Default `'PENDING'` | Status: `PENDING`, `APPROVED`, `REJECTED`, `PROCESSING`, `COMPLETED`, `FAILED`, `UNKNOWN` |
| `reference_id` | `VARCHAR(100)` | Not Null, Unique | Idempotency reference (`PAYOUT_{id}`) |
| `gateway_reference` | `VARCHAR(100)` | Nullable | External mock/gateway transaction reference |
| `failure_reason` | `VARCHAR(255)` | Nullable | Reason for rejection or failure |
| `created_at` | `TIMESTAMP` | Not Null | Request creation timestamp |
| `updated_at` | `TIMESTAMP` | Not Null | Last update timestamp |

## State Transitions
1. `PENDING` -> `APPROVED` (Admin approves -> executes mock payout -> `COMPLETED` or `FAILED` or `UNKNOWN`)
2. `PENDING` -> `REJECTED` (Admin rejects -> releases reserved funds to available balance)
3. `PROCESSING` -> `COMPLETED` (Mock gateway success -> converts reserved funds into completed debit ledger entry `PAYOUT_{id}`)
4. `PROCESSING` -> `FAILED` (Mock gateway failure -> releases reserved funds back to available balance)
5. `PROCESSING` -> `UNKNOWN` (Gateway timeout/uncertainty -> retains reservation for manual reconciliation)
