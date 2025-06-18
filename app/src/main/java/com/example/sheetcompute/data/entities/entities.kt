package com.example.sheetcompute.data.entities

import java.time.LocalDate
import java.time.LocalTime

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey val id: Int,
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
    val employeeId: String,
    val date: LocalDate,
    val clockIn: String, // e.g., "06:25 AM"
    val tardyMinutes: Int = 0 // e.g., 35 mins late
)

data class AttendanceRecord2(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: String,
    val employeeName: String,
    val date: LocalDate,
    val clockInTime: LocalTime?,
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val lateMinutes: Int = 0
)

@Entity("EmployeeStats")
data class EmployeeStats(
    @PrimaryKey val employeeId: String,

    val totalPresent: Int = 0,
    val totalAbsent: Int = 0,
    val totalLate: Int = 0,
    val extraDays: Int = 0, // weekends or holidays worked
    val lastUpdated: LocalDate

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
    @PrimaryKey(autoGenerate = true) val id: Int,
    val employeeId: String,
    val loginTime: Int, // store as minutes ( 570) to "HH:mm" format (e.g. "09:30")
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