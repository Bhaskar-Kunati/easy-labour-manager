# Research: Service Catalog and Search

**Feature Branch**: `005-service-catalog-search`

## Decision 1: Catalog Read Endpoints Accessibility

- **Decision**: Expose category and service browse/search endpoints (`/api/v1/categories`, `/api/v1/services`, `/api/v1/services/{id}`, `/api/v1/services/search`) as public read endpoints via API Gateway security configuration, while keeping admin write endpoints (`/api/v1/admin/...`) secured.
- **Rationale**: Customers need to browse and search the service catalog before authentication or booking without friction.
- **Alternatives Considered**: Requiring authentication for browsing (rejected as it adds unnecessary friction for discovery).

## Decision 2: Search Implementation Strategy

- **Decision**: Implement keyword search via Spring Data JPA repository derived query methods or JPQL (`findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase` or similar) in `ServiceRepository`.
- **Rationale**: Keeps database queries lightweight and avoids adding heavy external search engines (like Elasticsearch) for MVP catalog scope.
- **Alternatives Considered**: External search index (rejected as over-engineering for current scope).

## Decision 3: Entity Reuse and Boundaries

- **Decision**: Reuse existing `Service` and `ServiceCategory` entities and `CategoryRepository`, `ServiceRepository`, `CategoryService`, and `ServiceService`.
- **Rationale**: Models and repositories are already established in `catalog-service`, ensuring zero redundant database tables or conflicting entities.
- **Alternatives Considered**: Creating new catalog tables (rejected).
