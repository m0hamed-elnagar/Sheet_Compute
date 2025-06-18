package com.example.sheetcompute.domain.useCases

import androidx.paging.Pager
import com.example.sheetcompute.data.entities.AttendanceRecordUI
import com.example.sheetcompute.domain.repo.AttendanceRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDate

class GetAttendanceSummaryPagerUseCase(
    private val attendanceRepo: AttendanceRepo,
    private val workingDaysUseCase: CalculateWorkingDaysUseCase
) {

    suspend operator fun invoke(month: Int, year: Int, range: ClosedRange<LocalDate>
                                , pageSize: Int
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
