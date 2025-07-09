package com.example.sheetcompute.data.mappers

import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.data.entities.AttendanceSummary


    fun AttendanceSummary.mapToAttendanceRecordUI(

        totalWorkingDays: Int,
    ): AttendanceRecordUI {
        val absentCount = totalWorkingDays - presentDays

        return AttendanceRecordUI(
            id = id,
            name =name,
            month =month,
            year = year,
            absentCount = absentCount,
            totalTardyMinutes = totalTardyMinutes,
            presentDays = presentDays
        )
    }
