package com.example.sheetcompute.data.repo

import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.data.entities.HolidayRange
import java.time.LocalDate

class FakeHolidayRepo : HolidayRepoInterface {
    var holidays = mutableListOf<Holiday>()

    override suspend fun addHoliday(holiday: Holiday) {
        holidays.add(holiday)

    }
    override suspend fun updateHoliday(holiday: Holiday) {
        holidays.replaceAll { if (it.id == holiday.id) holiday else it }
    }
    override suspend fun deleteHoliday(holiday: Holiday) {
        holidays.removeIf { it.id == holiday.id }
    }
    override suspend fun getHolidaysByDateRange(startDate: LocalDate, endDate: LocalDate): List<Holiday> {
        return holidays.filter { !it.startDate.isAfter(endDate) && !it.endDate.isBefore(startDate) }
    }
    override suspend fun getHolidayDatesBetween(start: LocalDate, end: LocalDate): List<HolidayRange> {
        // Return HolidayRanges based on holidays that overlap with the [start, end] range
        return holidays
            .filter { !it.startDate.isAfter(end) && !it.endDate.isBefore(start) }
            .map { HolidayRange(it.startDate, it.endDate) }
    }
}
