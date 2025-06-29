package com.example.sheetcompute.data.local.room.daos


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.AttendanceSummary
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.data.entities.HolidayRange
import java.time.LocalDate

@Dao
interface HolidayDao {
    @Insert
    suspend fun addHoliday(holiday: Holiday)

    @Update
    suspend fun updateHoliday(holiday: Holiday)

    @Delete
    suspend fun deleteHoliday(holiday: Holiday)

    @Query("SELECT * FROM holidays WHERE startDate >= :startDate AND endDate <= :endDate ORDER BY startDate")
    suspend fun getHolidaysByDateRange(startDate: LocalDate, endDate: LocalDate): List<Holiday>

    @Query("SELECT startDate, endDate FROM holidays WHERE startDate <= :end AND endDate >= :start")
    suspend fun getHolidayRanges(start: LocalDate, end: LocalDate): List<HolidayRange>

}

@Dao
interface EmployeeDao {
    @Query("SELECT id FROM employees")
    suspend fun getAllEmployeeIds(): List<Long>

    @Query("SELECT * FROM employees")
    suspend fun getAllEmployees(): List<EmployeeEntity>

    @Query(
        """
        SELECT *
        FROM employees 
        WHERE (:query IS NULL OR :query = '' OR 
               name LIKE '%' || :query || '%' OR 
               id LIKE '%' || :query || '%')
        ORDER BY id ASC
    """
    )
    suspend fun getEmployees(query: String?): List<EmployeeEntity>
    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getEmployeeById(id: Long): EmployeeEntity?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(employees: List<EmployeeEntity>)
}

@Dao
interface AttendanceDao {
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
     WHERE employeeId = :employeeId AND date IN (:dates)
"""
    )
    suspend fun getEmployeeRecordsByDateList(
        employeeId: Long,
        dates: List<LocalDate>
    ): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_Record WHERE employeeId = :employeeId AND date BETWEEN :startDate AND :endDate ORDER BY date")
    suspend fun getEmployeeRecordsByDateRange(
        employeeId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<AttendanceRecord>

    @Insert
    suspend fun addAttendanceRecord(attendanceRecord: AttendanceRecord)

    @Query(
        """
        SELECT e.id AS id,
               e.name AS name,
               :month AS month,
               :year AS year,
               COUNT(ar.id) AS presentDays,
               SUM(ar.tardyMinutes) AS totalTardyMinutes
        FROM employees e
        LEFT JOIN attendance_Record ar
            ON e.id = ar.employeeId AND ar.date BETWEEN :startDate AND :endDate
        GROUP BY e.id
        ORDER BY totalTardyMinutes DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getAttendanceSummary(
        startDate: LocalDate,
        endDate: LocalDate,
        month: Int,
        year: Int,
        limit: Int,
        offset: Int
    ): List<AttendanceSummary>

}