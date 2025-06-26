package com.example.sheetcompute.domain.usecase

import androidx.paging.Pager
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.data.mappers.toEmployeeAttendanceRecord
import com.example.sheetcompute.data.repo.AttendanceRepo
import com.example.sheetcompute.domain.useCases.workingDays.GetNonWorkingDaysUseCase
import com.example.sheetcompute.ui.subFeatures.utils.filter
import java.time.LocalDate

class GetEmployeeAttendanceRecordsUseCase(
    private val attendanceRepo: AttendanceRepo,
    private val getNonWorkingDaysUseCase: GetNonWorkingDaysUseCase
) {

    suspend operator fun invoke(
        employeeId: Long,
        start: LocalDate,
        end: LocalDate
    ): List<EmployeeAttendanceRecord> {
        val holidays = getNonWorkingDaysUseCase(start, end)
        val workingDays = (start..end).filter { it !in holidays }

         val recordsFromDb = attendanceRepo.getEmployeeAttendanceRecordsByRange(
        employeeId = employeeId,
        startDate = start,
        endDate = end,
    )
            val recordMap = recordsFromDb.associateBy { it.date }
 return workingDays.map { date ->
            val record = recordMap[date]
            record?.toEmployeeAttendanceRecord(holidays .toList())
                ?: EmployeeAttendanceRecord(
                    id = date.toEpochDay() * -1,
                    employeeId = employeeId,
                    loginTime = "",
                    date = date,
                    lateDuration = 0,
                    status = AttendanceStatus.ABSENT
                )
        }
    }
}