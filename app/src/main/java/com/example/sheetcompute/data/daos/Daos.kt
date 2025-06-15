package com.example.sheetcompute.data.daos


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.entities.Holiday
import java.time.LocalDate
import java.util.Date

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
@Dao
interface EmployeeDao {
    @Query("SELECT id FROM employees")
    suspend fun getAllEmployeeIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(employees: List<EmployeeEntity>)
}

@Dao
interface EmployeeAttendanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(records: List<AttendanceRecord>): List<Long>
    //getAllEmployeeIds
    @Query("SELECT id FROM employees")
    suspend fun getAllEmployeeIds(): List<Int>
    //todo get attendance records by date paginated and sorted by time
    @Transaction
    @Query(
        """
    SELECT * FROM attendance_Record
    WHERE employeeId = :employeeId AND date >= :startDate AND date <= :endDate
    ORDER BY date ASC
    LIMIT :limit OFFSET :offset
"""
    )
    suspend fun getEmployeeRecordsByDateRangePaged(
        employeeId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        limit: Int,
        offset: Int
    ): List<AttendanceRecord>

    @Insert
    suspend fun addAttendanceRecord(attendanceRecord: AttendanceRecord)
}