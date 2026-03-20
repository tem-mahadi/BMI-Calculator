# BMI Calculator (Android)

An Android BMI tracking app with modern UI, dark mode support, BdApps OTP-based login flow, history tracking, and daily reminder notifications.

## Features

- BMI calculation with metric and imperial units
- Detailed BMI result cards (value, category, and guidance)
- Local BMI history tracking with Room database
- Progress dashboard (entry count, average BMI, trend, history list)
- Daily reminder notifications with custom reminder time
- Splash screen entry flow
- BdApps backend OTP authentication flow
- Dark mode compatible UI (DayNight theme)

## App Flow

1. Splash screen opens on app launch.
2. Authentication flow uses BdApps mobile number + OTP screens.
3. User reaches onboarding/start page.
4. BMI screen allows calculation and saving entries.
5. Progress screen shows history, trend, reminder settings, and logout.

## Tech Stack

- Language: Java
- UI: XML + Material Components
- Networking: Retrofit + Gson + OkHttp Logging Interceptor
- Local storage: Room
- Background tasks: WorkManager
- Min SDK: 24
- Target SDK: 34

## Project Structure (Key Parts)

- app/src/main/java/com/temmahadi/bmicalculator
  : Core app screens and flow (Splash, BMI, Progress, onboarding)

- app/src/main/java/com/temmahadi/bmicalculator/BdApps_Backend
  : BdApps OTP authentication integration and subscription/session helpers

- app/src/main/java/com/temmahadi/bmicalculator/data
  : Room entities, DAO, and database configuration

## Build and Run

### Prerequisites

- Android Studio (latest stable recommended)
- JDK compatible with Android Gradle Plugin setup
- Internet connection for dependency resolution

### Build (Debug)

```bash
./gradlew :app:assembleDebug
```

### Install on Device/Emulator

Use Android Studio Run, or install the generated APK from:

- app/build/outputs/apk/debug/

## Permissions

- INTERNET: for BdApps API requests
- POST_NOTIFICATIONS: for daily reminder notifications (Android 13+)

## Notes

- Reminder schedule and user auth/session state are persisted using SharedPreferences.
- BMI history is persisted locally using Room.

## Author

- Hasan Al Mahadi
