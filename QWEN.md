# Sheet Compute - Project Context

## Project Overview

**Sheet Compute** is an Android application built with Kotlin for managing employee attendance and timesheet data. The app parses Excel files containing attendance records, computes working days, tardiness, and generates attendance summaries. It features a local database for storing employee records, attendance data, and holiday information.

### Core Features
- **Excel Import & Parsing**: Parse attendance data from Excel (.xls) files using Apache POI
- **Employee Attendance Tracking**: Track clock-in times, tardiness, and attendance status
- **Attendance History**: View paged attendance summaries filtered by month/year
- **Holiday Calendar Management**: Manage holidays with weekend selection and date ranges
- **Search & Filter**: Search employees and filter attendance by date ranges
- **Export**: Export employee records to Excel format
- **Firebase Remote Config**: Feature flags for controlling functionality remotely

### Architecture Pattern
The project follows **Clean Architecture** principles with clear separation of concerns:

```
app/
├── data/                    # Data Layer
│   ├── entities/           # Room entities (EmployeeEntity, AttendanceRecord, Holiday)
│   ├── local/room/         # Room database, DAOs, TypeConverters
│   ├── mappers/            # Data-to-UI mappers
│   ├── paging/             # Paging 3 sources
│   └── repo/               # Repository implementations
│
├── domain/                  # Domain Layer
│   ├── di/                 # Hilt dependency injection modules
│   ├── excel/              # Excel parsing logic (ExcelParser, ExcelRowParser, calcClockIn)
│   └── useCases/           # Use case classes for business logic
│
└── ui/                      # Presentation Layer
    ├── features/           # Main app features (Fragments, ViewModels, Adapters)
    └── subFeatures/        # Shared utilities (dialogs, utils, spinners)
```

### Technology Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin 2.2.0 |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 35 |
| **Compile SDK** | 35 |
| **DI** | Hilt 2.57 |
| **Database** | Room 2.7.2 |
| **Async** | Coroutines 1.10.2 |
| **Navigation** | AndroidX Navigation Component 2.9.2 |
| **UI** | View Binding, Material Design, RecyclerView, ViewPager2 |
| **Paging** | Paging 3 (3.3.6) |
| **Testing** | JUnit, MockK, Truth, Turbine, kotlinx-coroutines-test |
| **Backend Services** | Firebase Remote Config |
| **Excel Processing** | Apache POI 3.17 (via jitpack) |
| **Build System** | Gradle 8.10.0 with Kotlin DSL, Version Catalogs |

## Building and Running

### Prerequisites
- Android Studio (latest)
- JDK 11+
- Android SDK 35

### Commands

**Build the project:**
```bash
./gradlew build
```

**Build Debug APK:**
```bash
./gradlew assembleDebug
```

**Build Release APK:**
```bash
./gradlew assembleRelease
```

**Run unit tests:**
```bash
./gradlew test
```

**Run instrumented tests:**
```bash
./gradlew connectedAndroidTest
```

**Run specific test:**
```bash
./gradlew test --tests "com.example.sheetcompute.domain.excel.ExcelParserTest"
```

**Clean build:**
```bash
./gradlew clean build
```

## Development Conventions

### Code Style
- Kotlin official coding style (`kotlin.code.style=official`)
- Package-by-feature organization in `ui/features/`
- Clean architecture layers (data, domain, ui)
- Use of sealed classes for result types (`ParseResult`)

### Architecture Patterns
- **MVVM with Clean Architecture**: ViewModels in `ui/features/`, UseCases in `domain/`, Repositories in `data/repo/`
- **Dependency Injection**: Hilt for DI with modules in `domain/di/`
- **Reactive UI**: LiveData for UI state, Kotlin Flow support
- **Pagination**: Paging 3 library for efficient data loading
- **Navigation**: Safe Args for type-safe navigation

### Testing Practices
- **Unit Tests**: Located in `src/test/` - comprehensive coverage for:
  - ViewModels (CalendarViewModel, SearchViewModel, DateFilterViewModel, etc.)
  - Use Cases (attendance, datetime, working days)
  - Excel parsing logic (ExcelParser, ExcelRowParser, calcClockIn)
  - Utilities (DateUtils, RangeUtils, ExcelFileSaver)
  - Mappers (AttendanceSummaryUIMapper, EmployeeAttendanceMapper)
- **Instrumented Tests**: Located in `src/androidTest/` - DAO tests (AttendanceDaoTest, EmployeeDaoTest)
- **Test Utilities**: MockPagingSource, MainDispatcherRule, LiveDataUtilTest
- **Test Libraries**: JUnit 4, MockK, Truth, Turbine (Flow testing), kotlinx-coroutines-test

### Key Implementation Details

#### Room Database
- Database version: 2
- Entities: `AttendanceRecord`, `Holiday`, `EmployeeEntity`
- Type converters for LocalDate
- Indices on frequently queried columns (employeeId, date)

#### Excel Parsing
- Uses Apache POI (version 3.17) via jitpack
- Parses `.xls` format (HSSF)
- Generates unique employee IDs
- Calculates tardiness based on configurable work start time
- Returns structured `ParseResultBundle` with employees, records, and errors

#### Working Days Calculation
- Separate use cases for counting working days and getting non-working days
- Weekend configuration stored in SharedPreferences
- Holiday awareness in working day calculations

#### Firebase Remote Config
- Used for feature flags (e.g., `excel_enabled`)
- Fetch interval: 0s in debug, 1 hour in release
- Async initialization in Application class

## Security Notes

⚠️ **Hardcoded Credentials**: The signing config in `app/build.gradle.kts` contains hardcoded keystore passwords (`android101`). This should be moved to environment variables or `local.properties` before committing to version control.

## Known Improvement Areas

1. **ProGuard/R8**: Minification is disabled (`isMinifyEnabled = false`) in release builds
2. **Security**: Keystore passwords exposed in build.gradle.kts
3. **Apache POI Version**: Using very old POI version (3.17 from 2017)
4. **Version Catalogs**: Some dependency versions use `latest` instead of explicit versions
5. **Navigation Graph**: No start destination arguments or deep links configured
6. **Error Handling**: Limited error handling in Excel parsing
7. **Database Migrations**: No explicit migration strategy defined
8. **Testing**: Missing integration tests for Excel import/export flow

## Git Branches

- `master` - Production branch
- `dev` - Development branch (current working branch)
- `origin/tests/excel-parsing-tests` - Test improvements
- `tests/viewmodels-core-` - ViewModel test enhancements

## Important Files

| File | Purpose |
|------|---------|
| `app/build.gradle.kts` | Main app build configuration |
| `gradle/libs.versions.toml` | Version catalog for dependencies |
| `app/src/main/AndroidManifest.xml` | App manifest with permissions and components |
| `app/src/main/java/.../App.kt` | Application class with Firebase init |
| `app/src/main/java/.../domain/di/*` | Hilt DI modules |
| `app/src/main/java/.../data/local/room/AppDatabase.kt` | Room database definition |
| `app/src/main/java/.../domain/excel/ExcelParser.kt` | Core Excel parsing logic |
| `app/src/main/res/navigation/main_nav_graph.xml` | Navigation graph |
| `app/proguard-rules.pro` | ProGuard rules for release builds |
