# Implementation Plan: Push Notification & In-App Messaging Center (Feature 017)

**Feature Branch**: `017-push-notification-messaging`
**Spec File**: [spec.md](spec.md)
**Created**: November 17, 2026

## Technical Context

- **Language / Runtime**: Java 19 (Spring Boot 3.2), Kotlin (Android app).
- **Architecture**: Event-driven microservices (`notification-service`), Kafka event consumption, MongoDB for notification storage and device token registry.
- **APIs**: REST endpoints secured with JWT (`common-lib` filter / Spring Security) and internal secret validation (`X-Internal-Service-Key`).
- **Client**: Android Jetpack Compose app with Firebase Messaging SDK integration, Admin Web broadcast interface.
- **Dependencies**: Feature 007 booking notifications, Feature 003 authentication/tokens, Feature 006/008/009 event producers (booking, payment, tracking).

## Constitution Check

- Compliant with project microservice architecture, Spring Boot conventions, Java 19 best practices, and security mandates (JWT, internal service key auth, no wildcard CORS with credentials).
- Reuses existing infrastructure (Kafka brokers, MongoDB, common-lib security utilities).

## Phase 0: Research & Architecture Decisions

1. **Firebase Cloud Messaging (FCM) Integration**: Use Firebase Admin SDK for Java to send multicast and targeted push notifications. Support offline/mock fallback when `firebase.credentials.path` is not provided in local development profile.
2. **Device Token Schema**: Store device tokens in MongoDB associated with `userId` and `userType` (CUSTOMER/WORKER), supporting multiple devices per user and automatic cleanup on FCM `NotRegistered` errors.
3. **Kafka Event Listeners**: Extend existing notification listeners in `notification-service` to process booking, payment, and tracking events, persisting in-app notifications and triggering FCM pushes.

## Phase 1: Design & Contracts

- **Data Model**: [data-model.md](data-model.md)
- **API Contracts**: [contracts/rest-api.yaml](contracts/rest-api.yaml)
- **Quickstart Guide**: [quickstart.md](quickstart.md)
