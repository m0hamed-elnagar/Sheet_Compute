package com.example.sheetcompute.domain.useCases.attendance

import androidx.paging.Pager
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.data.repo.AttendanceRepo
import com.example.sheetcompute.domain.useCases.workingDays.CountWorkingDaysUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetAttendanceSummaryPagerUseCase @Inject constructor(
    private val attendanceRepo: AttendanceRepo,
    private val workingDaysUseCase: CountWorkingDaysUseCase
) {
    suspend operator fun invoke(
        month: Int,
        year: Int,
        range: ClosedRange<LocalDate>,
        pageSize: Int
    ): Pager<Int, AttendanceRecordUI> {
        val workingDays = workingDaysUseCase(range.start, range.endInclusive)
        return attendanceRepo.getPagedAttendanceSummaries(
            month = month,
            year = year,
            range = range,
            totalWorkingDays = workingDays,
            pageSize = pageSize,
        )
    }
}
