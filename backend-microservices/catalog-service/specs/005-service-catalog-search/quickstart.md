# Quickstart Validation Guide: Service Catalog and Search

**Feature Branch**: `005-service-catalog-search`

## Prerequisites
- PostgreSQL running and accessible.
- `catalog-service` running.

## Validation Scenarios

### 1. Browse Categories (`GET /api/v1/categories`)
- **Request**: `GET /api/v1/categories`
- **Expected Outcome**: HTTP 200 OK with list of active service categories.

### 2. Browse Services with Category Filter (`GET /api/v1/services?categoryId=1`)
- **Request**: `GET /api/v1/services?categoryId=1`
- **Expected Outcome**: HTTP 200 OK with filtered list of services belonging to category 1.

### 3. Get Service Details (`GET /api/v1/services/{id}`)
- **Request**: `GET /api/v1/services/1`
- **Expected Outcome**: HTTP 200 OK with service details (price, inclusions, exclusions).

### 4. Search Services (`GET /api/v1/services/search?query=cleaning`)
- **Request**: `GET /api/v1/services/search?query=cleaning`
- **Expected Outcome**: HTTP 200 OK with matching services.
