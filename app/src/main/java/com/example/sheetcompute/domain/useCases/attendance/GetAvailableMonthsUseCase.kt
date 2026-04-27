package com.example.sheetcompute.domain.useCases.attendance

import com.example.sheetcompute.data.repo.AttendanceRepo
import javax.inject.Inject

class GetAvailableMonthsUseCase @Inject constructor(
    private val attendanceRepo: AttendanceRepo
) {
    suspend operator fun invoke(employeeId: Long? = null): List<String> {
        return attendanceRepo.getAvailableMonthStrings(employeeId)
    }
}
