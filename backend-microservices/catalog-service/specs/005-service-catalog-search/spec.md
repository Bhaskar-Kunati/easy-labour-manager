# Feature Specification: Service Catalog and Search in Catalog Service

**Feature Branch**: `005-service-catalog-search`

**Created**: 2026-10-04

**Status**: Draft

**Input**: User description: "Service Catalog and Search in catalog-service. Allow customers to browse service categories, view available labour/service offerings, view service details, and search/filter services by keyword, category, and relevant service attributes. Follow the existing Easy Labour project architecture, coding conventions, API Gateway/security model, database conventions, and catalog-service boundaries. Inspect the existing catalog-service implementation and overall DESIGN.md before defining requirements. Reuse existing models, repositories, services, and controllers where appropriate. Do not introduce duplicate entities, unnecessary database tables, or functionality belonging to booking-service, user-service, or worker management. Do not implement the feature yet."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Browse Service Categories (Priority: P1)

Customers can browse and retrieve all active service categories (e.g., Home Services, Cleaning, Plumbing, Electrical, Construction) available on the platform via `/api/v1/categories`.

**Why this priority**: Browsing service categories is the primary entry point for users discovering available services on the platform.

**Independent Test**: Can be fully tested by sending a `GET` request to `/api/v1/categories` and verifying that an HTTP 200 OK response is returned with the list of active service categories.

**Acceptance Scenarios**:

1. **Given** active service categories exist in the system, **When** a customer requests categories via `GET /api/v1/categories`, **Then** the system returns an HTTP 200 OK with the list of active categories.
2. **Given** a valid category ID, **When** a customer requests category details via `GET /api/v1/categories/{id}`, **Then** the system returns the specific category details with an HTTP 200 OK response.
3. **Given** an invalid or non-existent category ID, **When** category details are requested, **Then** the system returns an HTTP 404 Not Found response.

---

### User Story 2 - Browse and Filter Service Offerings (Priority: P1)

Customers can view available labour/service offerings and filter them by category ID or view service details by ID via `/api/v1/services`.

**Why this priority**: Viewing services and filtering by category enables customers to select specific labour services they wish to book.

**Independent Test**: Can be fully tested by sending a `GET` request to `/api/v1/services` (with or without `categoryId`) and verifying that matching active services are returned.

**Acceptance Scenarios**:

1. **Given** active services exist in the system, **When** a customer requests services via `GET /api/v1/services`, **Then** the system returns an HTTP 200 OK with the list of active services.
2. **Given** a category ID filter, **When** a customer requests services via `GET /api/v1/services?categoryId={id}`, **Then** the system returns only services belonging to that category.
3. **Given** a valid service ID, **When** a customer requests service details via `GET /api/v1/services/{id}`, **Then** the system returns detailed service attributes (pricing, inclusions, exclusions) with HTTP 200 OK.
4. **Given** an invalid service ID, **When** service details are requested, **Then** the system returns an HTTP 404 Not Found response.

---

### User Story 3 - Search Services by Keyword (Priority: P2)

Customers can search for services by keyword or query string via `/api/v1/services/search?query=...` to quickly locate specific labour skills or service types.

**Why this priority**: Keyword search improves user experience by allowing direct lookup of services without manual category browsing.

**Independent Test**: Can be fully tested by submitting a search query string to `/api/v1/services/search` and verifying that matching services are returned.

**Acceptance Scenarios**:

1. **Given** a search query matching service titles or descriptions, **When** a customer submits `GET /api/v1/services/search?query={term}`, **Then** the system returns matching active services with HTTP 200 OK.
2. **Given** a search query with no matching services, **When** searched, **Then** the system returns an empty list with HTTP 200 OK.

---

### Edge Cases

- What happens if search queries contain special characters or leading/trailing whitespace? (System sanitizes and handles query parameters safely).
- What happens if no active services exist in a category? (System returns an empty list).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a `GET /api/v1/categories` endpoint to list all active service categories.
- **FR-002**: System MUST provide a `GET /api/v1/categories/{id}` endpoint to retrieve specific category details.
- **FR-003**: System MUST provide a `GET /api/v1/services` endpoint to list active services, supporting optional filtering by `categoryId`.
- **FR-004**: System MUST provide a `GET /api/v1/services/{id}` endpoint to retrieve detailed service information.
- **FR-005**: System MUST provide a `GET /api/v1/services/search` endpoint supporting query parameter `query` for keyword search.
- **FR-006**: System MUST reuse existing models (`Service`, `ServiceCategory`), repositories (`ServiceRepository`, `CategoryRepository`), services (`ServiceService`, `CategoryService`), and controllers (`ServiceController`, `CategoryController`) without introducing duplicate entities or database tables.

### Security & Non-Functional Requirements

- **NFR-001**: Catalog read endpoints (`/api/v1/categories`, `/api/v1/services`, `/api/v1/services/search`) must be publicly accessible or properly routed through API Gateway as defined by catalog-service architecture.
- **NFR-002**: Admin management endpoints (`/api/v1/admin/...`) remain restricted to authorized admin roles.

### Success Criteria

- **SC-001**: 100% of valid category and service listing/search requests return correct data within established performance expectations (< 200ms).
- **SC-002**: Zero duplicate database tables or redundant entity models introduced.

### Key Entities

- **ServiceCategory**: Category entity containing `id`, `name`, `description`, `iconUrl`, `status`, etc.
- **Service**: Service offering entity containing `id`, `category` (ManyToOne), `name`, `description`, `price`, `pricingType`, `serviceIncludes`, `serviceExcludes`, `status`, etc.
