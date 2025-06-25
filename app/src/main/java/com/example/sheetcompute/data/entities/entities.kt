package com.example.sheetcompute.data.entities

import java.time.LocalDate

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val position: String = "",
    val department: String = "",
)

@Entity(
    tableName = "attendance_Record",
    indices = [
        Index(value = ["employeeId", "date"], unique = true), //  prevent duplicates + speed
        Index(value = ["date"]),
    ]
)

data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long,
    val date: LocalDate,
    val clockIn: String, // e.g., "06:25 AM"
    val tardyMinutes: Long = 0 // e.g., 35 mins late
)



enum class AttendanceStatus {
    PRESENT, ABSENT, LATE, EXTRA_DAY
}

data class AttendanceSummary(
    val id: Int,
    val name: String,
    val month: Int,
    val year: Int,
    val presentDays: Int,
    val totalTardyMinutes: Int = 0
)

data class AttendanceRecordUI(
    val id: Int,
    val name: String,
    val month: Int = 1,
    val year: Int = 2025,
    val absentCount: Int = 0,
    val totalTardyMinutes: Int = 0,
    val presentDays: Int = 0

)

@Entity(
    tableName = "employee_attendance_record",
    indices = [Index(value = ["employeeId", "date"], unique = true)]
)
data class EmployeeAttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val employeeId: Long,
    val loginTime: String,
    val date: LocalDate,
    val lateDuration: Long = 0L,
    val status: AttendanceStatus
)

@Entity(tableName = "holidays")
data class Holiday(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val name: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class HolidayRange(
    val startDate: LocalDate,
    val endDate: LocalDate
)