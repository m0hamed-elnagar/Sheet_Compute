package com.example.sheetcompute.data.local.daos


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sheetcompute.data.local.entities.Holiday
import java.time.LocalDate

@Dao
interface HolidayDao {
    @Insert
    suspend fun addHoliday(holiday: Holiday)

    @Update
    suspend fun updateHoliday(holiday: Holiday)

    @Delete
    suspend fun deleteHoliday(holiday: Holiday)

    @Query("SELECT * FROM holidays")
    suspend fun getAllHolidays(): List<Holiday>
    @Query("SELECT * FROM holidays WHERE startDate >= :startDate AND endDate <= :endDate ORDER BY startDate")
    suspend fun getHolidaysByDateRange(startDate: LocalDate, endDate: LocalDate): List<Holiday>

}