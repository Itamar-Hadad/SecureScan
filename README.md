<p align="center">
  <img width="110" alt="IMG_8253" src="https://github.com/user-attachments/assets/0876734c-46db-47fa-8189-66ccee5f4409" />
</p>
<h1 align="center"> SecureScan</h1>
<p align="center"><b>Unixi Mobile Assignment</b></p>

SecureScan is an Android application that demonstrates end to end mobile flow with:
- QR scanning (CameraX + ML Kit)
- Dynamic backend configuration from QR payload
- Backend API integration (`/qr/resolve`, `/auth/validate`)
- Authentication success/failure handling
- Main app with two tabs:
  - Home (user profile data)
  - Device Information (device/app metadata)

---

## Features
### 1) QR Scan Flow
- App opens to QR scanner screen.
- Scans QR token/config and resolves backend/user data.
- Shows loading state while resolving.
- Handles camera permission and retry behavior.
### 2) Authentication Flow
- Displays resolved user email.
- User enters password and submits to backend (`POST /auth/validate`).
- On failure: navigates to Error screen with retry/scan-again actions.
- On success: navigates to Success screen, then auto-redirects to Main app.

### 3) Main Application
- Bottom navigation with two tabs:
  - **Home**: Full Name, Email, Company, Department, User ID, Account Creation Date.
  - **Device Info**: Device Model, OS, OS Version, App Version, Manufacturer, Language/Locale.
### 4) UI / Theme Support
- Supports Light and Dark Mode with adaptive theming based on the system configuration.

### 5) Security/Networking Notes
- Password is not logged in request/response bodies.
- Network security config is enabled:
  - HTTPS first by default.
  - Local dev HTTP is allowed only for loopback/emulator hosts if needed.
---
## Tech Stack
- **Language:** Kotlin
- **Architecture:** MVVM (ViewModel + Repository)
- **Networking:** Retrofit + OkHttp + Gson
- **QR/Camera:** CameraX + ML Kit Barcode Scanning
- **UI:** Fragments + Navigation Component + ViewBinding + Material Components
---
## Backend API Used
- `POST /qr/resolve`
- `POST /auth/validate`
- `GET /demo/qr-tokens`
- `GET /health`
---
## Project Structure (high level)
- `ui/`:Fragments for QR/Auth/Error/Success/Main/Home/DeviceInfo
- `viewmodels/`: Shared `MainViewModel`
- `repository/`: `MainRepository` API orchestration
- `interfaces/`: Retrofit `ApiService`
- `model/`: DTOs and API result models
- `utilities/`: Retrofit client, parser, constants, helpers
---
## How to Run
### 1. Prerequisites
- Android Studio (latest stable)
- Android SDK / Emulator or physical device
- JDK 11+
- Running backend server (Docker or local)
### 2. Start backend
Run the provided backend server from the assignment instructions.
### 3. Configure QR
Generate or use demo QR values from:
- `GET /demo/qr-tokens`
Make sure QR content matches the app’s expected format for backend URL/token.
### 4. Run app
- Open project in Android Studio
- Let Gradle sync
- Run app on emulator/device
- Scan QR and complete auth flow
---
## Test Scenarios
- Valid QR → Auth screen appears with user email
- Wrong password → Error screen
- Correct password → Success screen → Main app
- Home tab shows resolved user info
- Device Info tab shows local device/app info
- Camera permission denied flow works
- Network failure shows unified error message
---
## Screenshots

### Splash Screen
<p align="center">
  <img src="screenshots/splashscreen.jpg" width="250"/>
</p>

### QR Scan Screen
<p align="center">
  <img src="screenshots/QrFragment.jpg" width="250"/>
</p>

### Authentication Screen
<p align="center">
  <img src="screenshots/authfragment.jpg" width="250"/>
</p>

### Error Screen
<p align="center">
  <img src="screenshots/errorfragment.jpg" width="250"/>
</p>

### Success Screen
<p align="center">
  <img src="screenshots/successfragment.jpg" width="250"/>
</p>

### Main Screen: Home Tab
<p align="center">
  <img src="screenshots/homefragment.jpg" width="250"/>
</p>

### Main Screen: Device Info Tab
<p align="center">
  <img src="screenshots/deviceinfofragment.jpg" width="250"/>
</p>
