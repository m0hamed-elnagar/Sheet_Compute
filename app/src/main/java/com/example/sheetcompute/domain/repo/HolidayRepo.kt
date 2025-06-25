package com.example.sheetcompute.domain.repo

import com.example.sheetcompute.domain.gateways.database.daos.HolidayDao
import com.example.sheetcompute.entities.Holiday
import com.example.sheetcompute.entities.HolidayRange
import com.example.sheetcompute.domain.gateways.database.roomDB.AppDatabase
import java.time.LocalDate


class HolidayRepo (
)  {
    private val database by lazy { AppDatabase.get() }
    private val holidayDao: HolidayDao by lazy { database.holidayDao() }
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