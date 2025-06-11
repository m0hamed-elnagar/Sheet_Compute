package com.example.sheetcompute.domain.repo

import com.example.sheetcompute.data.daos.HolidayDao
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.data.roomDB.AppDatabase
import java.time.LocalDate

interface HolidayRepository {
    suspend fun addHoliday(holiday: Holiday)
    suspend fun updateHoliday(holiday: Holiday)
    suspend fun deleteHoliday(holiday: Holiday)
    suspend fun getHolidaysByDateRange(startDate: LocalDate, endDate: LocalDate): List<Holiday>
}
class HolidayRepositoryImpl (
) : HolidayRepository {
    private val database by lazy { AppDatabase.get() }
    private val holidayDao: HolidayDao by lazy { database.holidayDao() }
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
}