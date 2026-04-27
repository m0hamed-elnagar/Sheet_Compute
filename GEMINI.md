# Sheet Compute - Project Context

## Project Overview
**Sheet Compute** is an Android application developed in Kotlin for managing employee attendance and timesheet data. The app enables users to import attendance records from Excel files, calculate working days and tardiness, and generate summaries. It utilizes a local Room database for persistence and integrates Firebase Remote Config for feature management.

### Key Features
- **Excel Processing**: Import and parse `.xls` attendance files using Apache POI.
- **Attendance Management**: Track clock-in times, calculate late durations, and monitor attendance status.
- **Holiday Calendar**: Customizable holiday management with support for date ranges and weekend selection.
- **Data Persistence**: Local storage of employees, attendance records, and holidays using Room.
- **History & Filtering**: Paged attendance summaries with month/year filtering and employee search.
- **Exporting**: Ability to export employee data back to Excel format.
- **Remote Configuration**: Feature toggling via Firebase Remote Config (e.g., `excel_enabled`).

## Technology Stack
- **Language**: Kotlin 2.2.0
- **Android SDK**: Min 26, Target 35
- **Architecture**: MVVM with Clean Architecture (Data, Domain, UI layers)
- **Dependency Injection**: Hilt 2.57
- **Database**: Room 2.7.2 with Paging 3 support
- **Asynchronous Programming**: Coroutines 1.10.2 & Flow
- **Navigation**: Jetpack Navigation Component 2.9.2
- **UI Components**: View Binding, Material 3, RecyclerView, ViewPager2, Custom Calendar View
- **Excel Handling**: Apache POI 3.17 (via jitpack: `com.github.SUPERCILEX.poi-android`)
- **Backend**: Firebase (Remote Config)
- **Testing**: JUnit 4, MockK, Truth, Turbine (Flow), kotlinx-coroutines-test

## Architecture & Directory Structure
The project is organized into logical layers to maintain a clean separation of concerns:

- `app/src/main/java/com/example/sheetcompute/`
  - `data/`: Data layer containing Room entities, DAOs, repositories, and Paging sources.
    - `entities/`: Room database models (`EmployeeEntity`, `AttendanceRecord`, `Holiday`).
    - `local/`: Room database definition and migrations.
    - `repo/`: Repository implementations.
  - `domain/`: Domain layer for business logic.
    - `di/`: Hilt modules for dependency injection.
    - `excel/`: Excel parsing and calculation logic.
    - `useCases/`: Reusable business logic units (e.g., `CountWorkingDaysUseCase`).
  - `ui/`: Presentation layer.
    - `features/`: UI modules organized by feature (Fragments, ViewModels, Adapters).
      - `attendanceHistory/`
      - `employeeAttendance/`
      - `holidaysCalendar/`
      - `settingFragment/`
    - `subFeatures/`: Shared UI components and utilities.

## Building and Running
The project uses Gradle with Kotlin DSL and Version Catalogs.

### Common Commands
- **Assemble Debug APK**: `./gradlew assembleDebug`
- **Run Unit Tests**: `./gradlew test`
- **Run Instrumented Tests**: `./gradlew connectedAndroidTest`
- **Clean Build**: `./gradlew clean build`
- **Kotlin Linting**: `./gradlew :app:lintDebug` (Note: Run from the `android` root)

### Prerequisites
- Android Studio (Koala or later recommended)
- JDK 11
- Android SDK 35

## Development Conventions
- **Clean Architecture**: Strictly separate UI, Domain, and Data layers. Use-cases should contain the core business logic.
- **MVVM**: ViewModels should expose state via `LiveData` or `Flow`. Fragments should be responsible for UI observation and interaction.
- **Dependency Injection**: Use `@Inject` for dependencies and define Hilt modules in the `domain/di` package.
- **Testing**: 
  - Write unit tests for all UseCases and ViewModels.
  - Use `MockK` for mocking and `Truth` for assertions.
  - Use `MainDispatcherRule` for testing coroutines in ViewModels.
- **Hardcoded Secrets**: Note that some signing credentials are currently in `app/build.gradle.kts`. These should ideally be moved to `local.properties` or environment variables in a production environment.

## Important Implementation Details
- **Excel Parsing**: Handled in `domain/excel/ExcelParser.kt`. It specifically targets the `.xls` format.
- **Date Handling**: Utilizes `java.time.LocalDate` throughout the project, with Room type converters.
- **Tardiness Calculation**: Logic is configurable, typically comparing clock-in times against a standard start time defined in use cases or settings.
- **Feature Flags**: Controlled via `App.kt` using Firebase Remote Config.
