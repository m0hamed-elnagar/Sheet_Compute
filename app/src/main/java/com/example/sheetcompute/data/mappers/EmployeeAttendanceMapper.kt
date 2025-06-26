package com.example.sheetcompute.data.mappers


import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import java.time.LocalDate

fun AttendanceRecord.toEmployeeAttendanceRecord(holidays: List<LocalDate>): EmployeeAttendanceRecord {
    val status = when {
        date in holidays -> AttendanceStatus.EXTRA_DAY
        tardyMinutes > 0 -> AttendanceStatus.LATE
        else -> AttendanceStatus.PRESENT
    }

    return EmployeeAttendanceRecord(
        id = id,
        employeeId = employeeId,
        loginTime = clockIn,
        date = date,
        lateDuration = tardyMinutes,
        status = status
    )
}
