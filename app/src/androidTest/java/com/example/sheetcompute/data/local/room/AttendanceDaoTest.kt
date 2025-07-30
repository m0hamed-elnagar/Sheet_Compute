package com.example.sheetcompute.data.local.room

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.local.room.daos.AttendanceDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
@SmallTest
class AttendanceDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var dao: AttendanceDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room
            .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.attendanceDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testInsertAndGetAttendanceRecord() = runTest {
        val record = AttendanceRecord(
            id = 1L,
            employeeId = 1L,
            date = LocalDate.of(2024, 7, 1),
            clockIn = "06:30 AM",
            tardyMinutes = 5
        )
        dao.insertAll(listOf(record))
        val records = dao.getEmployeeRecordsByDateRange(
            employeeId = 1L,
            startDate = LocalDate.of(2024, 7, 1),
            endDate = LocalDate.of(2024, 7, 1)
        )
        assertEquals(1, records.size)
        assertEquals(5, records[0].tardyMinutes)
    }

    @Test
    fun testGetEmployeeRecordsByDateList() = runTest {
        val record1 = AttendanceRecord(1L, 1L, LocalDate.of(2024, 7, 1), "",0)
        val record2 = AttendanceRecord(2L, 1L, LocalDate.of(2024, 7, 2), "",10)
        dao.insertAll(listOf(record1, record2))
        val records = dao.getEmployeeRecordsByDateList(
            employeeId = 1L,
            dates = listOf(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 2))
        )
        assertEquals(2, records.size)
    }

    @Test
    fun testAddAttendanceRecord() = runTest {
        val record = AttendanceRecord(3L, 2L, LocalDate.of(2024, 7, 3), "",15)
        dao.addAttendanceRecord(record)
        val records = dao.getEmployeeRecordsByDateRange(2L, LocalDate.of(2024, 7, 3), LocalDate.of(2024, 7, 3))
        assertEquals(1, records.size)
        assertEquals(15, records[0].tardyMinutes)
    }

    @Test
    fun testInsertAllIgnoresDuplicates() = runTest {
        val record1 = AttendanceRecord(4L, 3L, LocalDate.of(2024, 7, 4), "", 0)
        val record2 = AttendanceRecord(4L, 3L, LocalDate.of(2024, 7, 4), "", 0) // duplicate id
        val result = dao.insertAll(listOf(record1, record2))
        assertEquals(1, result.filter { it != -1L }.size)
        val records = dao.getEmployeeRecordsByDateRange(3L, LocalDate.of(2024, 7, 4), LocalDate.of(2024, 7, 4))
        assertEquals(1, records.size)
    }

    @Test
    fun testGetAttendanceSummary() = runTest {
        // Insert employees and attendance records
        val employee1 = com.example.sheetcompute.data.entities.EmployeeEntity(10L, "Emp1")
        val employee2 = com.example.sheetcompute.data.entities.EmployeeEntity(11L, "Emp2")
        db.employeeDao().insertAll(listOf(employee1, employee2))
        val rec1 = AttendanceRecord(5L, 10L, LocalDate.of(2024, 7, 1), "", 5)
        val rec2 = AttendanceRecord(6L, 10L, LocalDate.of(2024, 7, 2), "", 10)
        val rec3 = AttendanceRecord(7L, 11L, LocalDate.of(2024, 7, 1), "", 0)
        dao.insertAll(listOf(rec1, rec2, rec3))
        val summary = dao.getAttendanceSummary(
            startDate = LocalDate.of(2024, 7, 1),
            endDate = LocalDate.of(2024, 7, 31),
            month = 7,
            year = 2024,
            limit = 10,
            offset = 0
        )
        assertEquals(2, summary.size)
        val emp1Summary = summary.find { it.id == 10L }
        assertNotNull(emp1Summary)
        assertEquals(2, emp1Summary?.presentDays)
        assertEquals(15L, emp1Summary?.totalTardyMinutes)
        val emp2Summary = summary.find { it.id == 11L }
        assertNotNull(emp2Summary)
        assertEquals(1, emp2Summary?.presentDays)
        assertEquals(0L, emp2Summary?.totalTardyMinutes)
    }
}
