package com.example.sheetcompute.domain.useCases.attendance

import androidx.paging.Pager
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.domain.repo.AttendanceRepo
import com.example.sheetcompute.domain.useCases.workingDays.GetNonWorkingDaysUseCase
import java.time.LocalDate

class GetEmployeeRecordsPagerUseCase(
    private val attendanceRepo: AttendanceRepo,
    private val nonWorkingDaysUseCase: GetNonWorkingDaysUseCase
) {

    suspend operator fun invoke(
        employeeId: Long,
        start: LocalDate,
        end: LocalDate
    ): Pager<Int, EmployeeAttendanceRecord> {
      val nonWorkingDays =  nonWorkingDaysUseCase(start, end)

     return   attendanceRepo.getEmployeeAttendanceRecordsByRange(
        employeeId = employeeId,
        startDate = start,
        endDate = end,
        holidays = nonWorkingDays.toList()
    )
    }
}