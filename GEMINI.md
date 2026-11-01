# Easy Labour Manager - Instructional Context

This project is a production-grade Labour Management System featuring a microservices-based backend and a modern Android application.

## Project Overview

The system is designed with an event-driven microservices architecture to manage labour bookings, payments, tracking, and notifications.

### Architecture Highlights
- **Backend**: Spring Boot 3.2 (Java 19), Microservices.
- **Messaging**: Apache Kafka for asynchronous communication between services.
- **Storage**:
  - **PostgreSQL**: Relational data for Auth, Users, Bookings, Payments, and Reviews.
  - **MongoDB**: Unstructured data for Tracking history and Notifications.
- **API Gateway**: Spring Cloud Gateway (Port 8080) for centralized routing.
- **Android**: Kotlin, Jetpack Compose, MVVM + Clean Architecture, Hilt DI, Retrofit.
- **Deployment**: Docker Compose for local development; Kubernetes for production-like environments.

## Directory Structure

- `/android-app`: Jetpack Compose Android application.
- `/backend-microservices`: Spring Boot microservices and shared libraries.
- `/devops`: Infrastructure configurations including Docker, Kubernetes (K8s), and Nginx.

## Building and Running

### Infrastructure (PostgreSQL, MongoDB, Kafka)
Start the required infrastructure using Docker Compose:
```powershell
cd devops/docker
docker-compose up -d
```

### Backend Microservices
The backend consists of multiple services: `auth-service`, `user-service`, `booking-service`, `tracking-service`, `payment-service`, `notification-service`, `review-service`, and `api-gateway`.

**Start all services at once (Windows/PowerShell):**
```powershell
./run-all-services.ps1
```

**Run a specific service:**
```powershell
cd backend-microservices
./gradlew :<service-name>:bootRun
```

**Build all services:**
```powershell
cd backend-microservices
./gradlew build
```

### Android Application
1. Open the `/android-app` folder in Android Studio.
2. Add your Google Maps API key to `local.properties`: `MAPS_API_KEY=your_key_here`.
3. Use Gradle to build and run the app on an emulator or physical device.

### Kubernetes Deployment
1. **Apply Infrastructure**:
   ```bash
   kubectl apply -f devops/k8s/database-config.yaml
   kubectl apply -f devops/k8s/infrastructure.yaml
   ```
2. **Deploy Services**:
   ```bash
   kubectl apply -f devops/k8s/
   ```

## Development Conventions

### Backend
- **Module Structure**: Common code is shared via the `common-lib` module.
- **DTOs**: Shared DTOs should be placed in `common-lib`.
- **API Gateway**: All external requests should go through the API Gateway on port 8080.

### Android
- **UI**: Built entirely with Jetpack Compose.
- **Dependency Injection**: Uses Hilt for DI.
- **Network**: Retrofit for API calls, Gson for serialization.
- **State Management**: Uses Flow and StateFlow within ViewModels.

## Key Files
- `README.md`: General project documentation and setup.
- `backend-microservices/api-gateway/src/main/resources/application.yml`: Gateway routing rules.
- `devops/docker/docker-compose.yml`: Infrastructure definitions for local development.
- `android-app/app/src/main/java/com/easylabour/app/ui/navigation/AppNavigation.kt`: Android app routing.

# Coding Instructions

## Response style
- Reply with code only unless I explicitly ask for an explanation.
- If an explanation is required, keep it under 100 words.
- Return only the changed method or class unless I ask for the complete file.
- Do not rewrite unchanged code.
- Keep responses concise.

## Code quality
- Follow Java 19 best practices.
- Use Spring Boot conventions.
- Prefer clean, readable code.
- Avoid unnecessary comments.

## Before coding
- Ask for missing files instead of making assumptions.
- Work only with the files I provide.
- Do not request the entire project unless it is genuinely required.

## Output
- Show the minimal diff needed.
- Preserve existing formatting and style.

Always follow these rules when running this project locally:

You are working on a machine with 8 GB RAM. Optimize resource usage and avoid starting all microservices at once.

Before starting anything:
- Check which infrastructure services are already running (Kafka, databases, Redis, Docker containers).
- Never start duplicate instances.
- If Kafka is already available on localhost:9092, reuse it instead of starting another Kafka broker.

For Spring Boot microservices:
- Always start services with limited JVM memory:
    - Default: `-Xms128m -Xmx256m`
    - Use `-Xms256m -Xmx512m` only if a service fails due to insufficient heap.
- Never start services with default JVM heap settings.

When running services:
- Do NOT launch all microservices simultaneously.
- Identify only the services required for the current task.
- Start services sequentially.
- Wait for each service to become healthy before starting the next one.

Preferred startup order:
1. Infrastructure dependencies (only if not already running)
2. Config/Discovery services
3. Required backend services
4. API Gateway
5. Frontend (if required)

Default development startup:
- Start only `auth-service`,`api-gateway`,`user-service`,`booking-gateway` and `review-service` unless additional services are explicitly required.

When using Gradle:
- Always use JVM limits:
  `bootRun --jvm-args="-Xms128m -Xmx256m"`

When creating IntelliJ run configurations:
- Always add VM options:
  `-Xms128m -Xmx256m`

If startup fails due to:
- "The paging file is too small"
- "Java Runtime Environment cannot allocate memory"
- "OutOfMemoryError"

Do not retry starting all services. Instead:
1. Stop unnecessary processes/services.
2. Reduce JVM memory.
3. Start only the required services.

Always explain which services you are starting and the expected memory usage before launching them.
