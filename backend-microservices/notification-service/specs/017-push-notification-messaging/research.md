# Research: Push Notification & In-App Messaging Center (Feature 017)

## Decisions & Rationale

1. **FCM Java Admin SDK vs REST API**:
   - *Decision*: Use Firebase Admin SDK (`firebase-admin`).
   - *Rationale*: Type-safe, official Google library supporting credential initialization, error handling (`FirebaseMessagingException`), and batch/multicast messaging.
2. **Local Development Fallback**:
   - *Decision*: Conditional `@Bean` initialization for `FirebaseMessaging` service. If Firebase initialization fails or configuration is absent in dev mode, provide a mock logging implementation.
   - *Rationale*: Prevents developer startup blocks when GCP/Firebase credentials are not present locally.
3. **MongoDB Document Design**:
   - *Decision*: Separate collections for `notifications` and `device_tokens`.
   - *Rationale*: Efficient querying of unread messages per user and fast token lookup by `userId` during event dispatch.
