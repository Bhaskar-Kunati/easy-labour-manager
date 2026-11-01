# Data Model: Service Catalog and Search

**Feature Branch**: `005-service-catalog-search`

## Entities

### 1. ServiceCategory
- **Description**: Represents a grouping of related labour services (e.g., Home Services, Cleaning).
- **Key Attributes**:
  - `id` (Long): Primary key.
  - `name` (String): Category name (required, unique).
  - `description` (String): Category description.
  - `iconUrl` (String): Icon image URL.
  - `status` (ServiceStatus / String): e.g., `ACTIVE`, `INACTIVE`.

### 2. Service
- **Description**: Represents a specific labour service offering available for booking.
- **Key Attributes**:
  - `id` (Long): Primary key.
  - `category` (ServiceCategory): ManyToOne relationship to `ServiceCategory`.
  - `name` (String): Service name (required).
  - `description` (String): Detailed service description.
  - `price` (BigDecimal): Price of the service.
  - `pricingType` (PricingType / String): e.g., `FIXED`, `HOURLY`.
  - `serviceIncludes` (String / List): What is included in the service.
  - `serviceExcludes` (String / List): What is excluded.
  - `status` (ServiceStatus / String): e.g., `ACTIVE`, `INACTIVE`.
