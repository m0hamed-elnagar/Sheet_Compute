package com.example.sheetcompute.data.repo

import com.example.sheetcompute.data.local.room.daos.HolidayDao
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.data.entities.HolidayRange
import com.example.sheetcompute.data.local.room.AppDatabase
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton

class HolidayRepo  @Inject constructor(
    private val holidayDao: HolidayDao
)  {
     suspend fun addHoliday(holiday: Holiday) {
        holidayDao.addHoliday(holiday)
    }

     suspend fun updateHoliday(holiday: Holiday) {
        holidayDao.updateHoliday(holiday)
    }

     suspend fun deleteHoliday(holiday: Holiday) {
        holidayDao.deleteHoliday(holiday)
    }


     suspend fun getHolidaysByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Holiday> {
        return holidayDao.getHolidaysByDateRange(startDate, endDate)
    }
    suspend fun getHolidayDatesBetween(start: LocalDate, end: LocalDate): List<HolidayRange> {
        val ranges = holidayDao.getHolidayRanges(start, end)
        return ranges
    }
}