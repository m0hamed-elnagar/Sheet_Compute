package com.example.sheetcompute.data.repo

import com.example.sheetcompute.data.local.room.daos.HolidayDao
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.data.entities.HolidayRange
import com.example.sheetcompute.data.local.room.AppDatabase
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
interface HolidayRepoInterface {
    suspend fun addHoliday(holiday: Holiday)
    suspend fun updateHoliday(holiday: Holiday)
    suspend fun deleteHoliday(holiday: Holiday)
    suspend fun getHolidaysByDateRange(startDate: LocalDate, endDate: LocalDate): List<Holiday>
    suspend fun getHolidayDatesBetween(start: LocalDate, end: LocalDate): List<HolidayRange>
}
@Singleton

class HolidayRepo @Inject constructor(
    private val holidayDao: HolidayDao
): HolidayRepoInterface {
    override suspend fun addHoliday(holiday: Holiday) {
        holidayDao.addHoliday(holiday)
    }

    override suspend fun updateHoliday(holiday: Holiday) {
        holidayDao.updateHoliday(holiday)
    }

    override suspend fun deleteHoliday(holiday: Holiday) {
        holidayDao.deleteHoliday(holiday)
    }


    override suspend fun getHolidaysByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Holiday> {
        return holidayDao.getHolidaysByDateRange(startDate, endDate)
    }

    override suspend fun getHolidayDatesBetween(start: LocalDate, end: LocalDate): List<HolidayRange> {
        val ranges = holidayDao.getHolidayRanges(start, end)
        return ranges
    }
}