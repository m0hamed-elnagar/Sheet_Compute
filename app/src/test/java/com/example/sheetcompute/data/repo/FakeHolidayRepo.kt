package com.example.sheetcompute.data.repo

import androidx.lifecycle.MutableLiveData
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.data.entities.HolidayRange
import java.time.LocalDate

class FakeHolidayRepo : HolidayRepoInterface {
    var holidays = mutableListOf(
        Holiday(1, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1), "New Year"),
        Holiday(2, LocalDate.of(2025, 1, 5), LocalDate.of(2025, 1, 6), "Holiday 2"),
        Holiday(3, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 12), "Holiday 3"),
    )
    var holidaysLiveData = MutableLiveData<List<Holiday>>(holidays)
    override suspend fun addHoliday(holiday: Holiday) {
        holidays.add(holiday)
        holidaysLiveData.postValue(holidays.toList())

    }
fun observeHolidays(): MutableLiveData<List<Holiday>> {
        return holidaysLiveData
    }
    override suspend fun updateHoliday(holiday: Holiday) {
        holidays.replaceAll { if (it.id == holiday.id) holiday else it }
        holidaysLiveData.postValue(holidays.toList())
    }

    override suspend fun deleteHoliday(holiday: Holiday) {
        holidays.removeIf { it.id == holiday.id }
        holidaysLiveData.postValue(holidays.toList())
    }

    override suspend fun getHolidaysByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Holiday> {
        return holidays.filter { !it.startDate.isAfter(endDate) && !it.endDate.isBefore(startDate) }
    }
    fun clear() {
        holidays = mutableListOf(
            Holiday(1, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1), "New Year"),
            Holiday(2, LocalDate.of(2025, 1, 5), LocalDate.of(2025, 1, 6), "Holiday 2"),
            Holiday(3, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 12), "Holiday 3"),
        )
        holidaysLiveData.postValue(holidays.toList())
    }
    override suspend fun getHolidayDatesBetween(
        start: LocalDate,
        end: LocalDate
    ): List<HolidayRange> {
        // Return HolidayRanges based on holidays that overlap with the [start, end] range
        return holidays
            .filter { !it.startDate.isAfter(end) && !it.endDate.isBefore(start) }
            .map { HolidayRange(it.startDate, it.endDate) }
    }
}
