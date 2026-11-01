# EasyLabour Android App

This is the Android mobile application for the EasyLabour Manager platform, built using modern Android development practices.

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Maps**: Google Maps SDK for Android (Compose version)
- **Async Processing**: Kotlin Coroutines & Flow

## Project Structure
- `di`: Hilt modules for dependency injection.
- `data`:
    - `api`: Retrofit service interfaces.
    - `repository`: Data layer implementations.
    - `model`: DTOs matching the backend microservices.
- `ui`:
    - `theme`: Material 3 design system setup.
    - `navigation`: Jetpack Navigation Compose setup.
    - `screens`: All UI screens (Login, Home, Tracking, etc.).
- `viewmodel`: ViewModels for managing UI state and business logic.

## Setup Instructions

1. **Prerequisites**:
   - Android Studio Giraffe or newer.
   - JDK 17.
   - Google Maps API Key.

2. **Configuration**:
   - Open `android-app/` in Android Studio.
   - Add your Google Maps API Key and Backend IP to `local.properties`:
     ```properties
     MAPS_API_KEY=YOUR_API_KEY_HERE
     API_BASE_URL=http://YOUR_MACHINE_IP:8080/
     ```
   - **Note**: If using a physical device, both the device and your computer must be on the same network. Use your computer's local IP (e.g., `192.168.x.x`). For the Android emulator, you can use `http://10.0.2.2:8080/`.

3. **Build & Run**:
   - Sync Gradle.
   - Run the `app` module on an emulator or physical device.

## Features Implemented
- **Authentication**: Login and Registration screens with Hilt & Retrofit.
- **Customer Dashboard**: View bookings and navigate to tracking.
- **Labour Dashboard**: View and manage job requests.
- **Live Tracking**: Google Maps integration for real-time location (Mocked).
- **Material 3**: Modern UI with dynamic color support and Material 3 components.
