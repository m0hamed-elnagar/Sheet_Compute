package com.example.sheetcompute.domain.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Database
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.paging.EmployeeAttendanceRecordsPagingSource
import com.example.sheetcompute.data.roomDB.AppDatabase
import java.time.LocalDate
import java.util.Date

class AttendanceRepo {
    private val database by lazy { AppDatabase.get() }

    private val attendanceDao by lazy { database.EmployeeAttendanceDao() }
    fun getEmployeeAttendanceRecordsByRange(
        employeeId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Pager<Int, AttendanceRecord> {

        return Pager(
            config = PagingConfig(
                pageSize = 4,
                enablePlaceholders = true,
                maxSize = 12
            ),
            pagingSourceFactory = {
                EmployeeAttendanceRecordsPagingSource(attendanceDao, employeeId, startDate, endDate)
            })
    }
    suspend fun insertRecords(records: List<AttendanceRecord>):Int{ return attendanceDao.insertAll(records).count { it != -1L }}
// get the working days  for the  range  once
    // use it to get the data and calc the presents and absent

}