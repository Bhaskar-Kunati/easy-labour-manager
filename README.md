# Easy Labour Manager - Production-Grade System

Complete production-grade Labour Management System featuring an event-driven microservices backend and a modern Jetpack Compose Android application.

## 🏗 Architecture Overview
- **Backend**: Spring Boot 3.2, Java 19, Microservices.
- **Messaging**: Apache Kafka for asynchronous communication (Booking -> Payment -> Notification).
- **Databases**: 
  - PostgreSQL for relational data (Auth, Users, Bookings, Payments, Reviews).
  - MongoDB for unstructured data (Tracking history, Notifications).
- **API Gateway**: Spring Cloud Gateway for centralized routing and security.
- **Android**: Kotlin, Jetpack Compose, MVVM + Clean Architecture, Hilt, Retrofit.
- **DevOps**: Docker, Docker Compose, Kubernetes (Native Service Discovery).

## 📁 Project Structure
- `/android-app`: Jetpack Compose Android application.
- `/backend-microservices`: Modular Spring Boot services.
- `/devops`: Docker, Kubernetes manifests, and Nginx configuration.

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- JDK 19+
- Android Studio (for mobile app)
- Gradle 8.x

### 1. Backend Setup (Local with Docker Compose)
1. Start infrastructure (Postgres, Mongo, Kafka):
   ```bash
   cd devops/docker
   docker-compose up -d
   ```
2. Build and run services:
   ```bash
   cd backend-microservices
   ./gradlew bootRun --parallel
   ```

### 2. Android App Setup
1. Open the `/android-app` folder in Android Studio.
2. Ensure you have a Google Maps API key in `local.properties`.
3. Build and run on an emulator or physical device.

### 3. Kubernetes Deployment
1. Apply infrastructure manifests:
   ```bash
   kubectl apply -f devops/k8s/database-config.yaml
   kubectl apply -f devops/k8s/infrastructure.yaml
   ```
2. Deploy microservices:
   ```bash
   kubectl apply -f devops/k8s/
   ```

## 📡 API Endpoints (Gateway: 8080)
- **Auth**: `POST /auth/register`, `POST /auth/login`
- **Users**: `GET /users/profile/{id}`, `GET /users/search?skill=plumber`
- **Bookings**: `POST /bookings/create`, `GET /bookings/{id}`
- **Tracking**: `GET /tracking/history/{bookingId}`
- **Payments**: `GET /payments/booking/{bookingId}`
- **Notifications**: `GET /notifications/user/{userId}`
- **Reviews**: `POST /reviews`, `GET /reviews/ratee/{rateeId}`

## 🛠 Tech Stack Details
- **Backend**: Spring Security + OAuth2, Spring Data JPA, Spring Data MongoDB.
- **Android**: Material 3, Navigation Compose, Hilt DI, Flow & StateFlow.
- **Realtime**: WebSockets in Tracking Service for live location.

## 📄 License
This project is for demonstration and production-grade template purposes.
