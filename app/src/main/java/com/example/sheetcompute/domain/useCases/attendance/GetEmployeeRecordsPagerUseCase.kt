package com.example.sheetcompute.domain.usecase

import androidx.paging.Pager
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.data.mappers.toEmployeeAttendanceRecord
import com.example.sheetcompute.data.repo.AttendanceRepo
import com.example.sheetcompute.domain.useCases.workingDays.GetNonWorkingDaysUseCase
import com.example.sheetcompute.ui.subFeatures.utils.filter
import com.example.sheetcompute.ui.subFeatures.utils.map
import java.time.LocalDate
import kotlin.collections.containsKey
import kotlin.text.get
import kotlin.times

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

        val recordsFromDb = attendanceRepo.getEmployeeAttendanceRecordsByRange(
            employeeId = employeeId,
            startDate = start,
            endDate = end
        )
        val recordMap = recordsFromDb.associateBy { it.date }
        return (start..end).map { date ->


            when {
                recordMap.containsKey(date) -> {
                    // ✅ Present — use real data
                    recordMap[date]!!.toEmployeeAttendanceRecord(holidays.toList())
                }

                else -> {
                    // ❌ Absent — working day but no record
                    EmployeeAttendanceRecord(
                        id = date.toEpochDay() * -1,
                        employeeId = employeeId,
                        loginTime = "",
                        date = date,
                        lateDuration = 0,
                        status = AttendanceStatus.ABSENT
                    )
                }
            }
        }.sortedBy { it.date }
    }
}
