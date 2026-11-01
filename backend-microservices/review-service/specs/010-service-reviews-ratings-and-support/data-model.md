# Data Model: Service Reviews, Ratings & Customer Support

**Branch**: `010-service-reviews-ratings-and-support` | **Date**: October 5, 2026

## Entities

### 1. Review
Represents a customer rating and feedback comment for a completed booking.
- `id` (Long / Long identifier, Primary Key)
- `bookingId` (String, Unique, Not Null) - Links to booking
- `customerId` (String, Not Null) - Authenticated customer who authored the review
- `workerId` (String, Not Null) - Worker who performed the service
- `serviceId` (String, Nullable) - Catalog service ID
- `rating` (Integer, Not Null) - Star rating between 1 and 5
- `comment` (String, Nullable) - Optional feedback comment
- `createdAt` (LocalDateTime, Not Null) - Timestamp of review creation

### 2. SupportTicket
Represents a customer support request or complaint.
- `id` (Long / Long identifier, Primary Key)
- `ticketId` (String, Unique, Not Null) - Public ticket reference
- `bookingId` (String, Nullable) - Associated booking ID
- `customerId` (String, Not Null) - Customer who opened the ticket
- `subject` (String, Not Null) - Ticket subject / title
- `message` (String, Not Null) - Ticket description / complaint details
- `status` (String, Not Null) - Status (e.g., OPEN, IN_PROGRESS, RESOLVED, CLOSED)
- `createdAt` (LocalDateTime, Not Null) - Timestamp of creation
- `updatedAt` (LocalDateTime, Nullable) - Timestamp of last update
