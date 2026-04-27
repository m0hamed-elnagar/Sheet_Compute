package com.example.sheetcompute.data.local.room

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.data.local.room.daos.EmployeeDao
import com.example.sheetcompute.data.local.room.daos.HolidayDao
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
class EmployeeDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var dao: EmployeeDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room
            .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.employeeDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testInsertAndGetEmployee() = runTest {
        val employee = com.example.sheetcompute.data.entities.EmployeeEntity(
            id = 1L,
            name = "Employee 1"
        )
        dao.insertAll(listOf(employee))
        val allEmployees = dao.getAllEmployees()
        assertEquals(1, allEmployees.size)
        assertEquals("Employee 1", allEmployees[0].name)
        val byId = dao.getEmployeeById(1L)
        assertNotNull(byId)
        assertEquals("Employee 1", byId?.name)
    }

    @Test
    fun testGetAllEmployeeIds() = runTest {
        val employees = listOf(
            com.example.sheetcompute.data.entities.EmployeeEntity(1L, "Employee A"),
            com.example.sheetcompute.data.entities.EmployeeEntity(2L, "Employee B")
        )
        dao.insertAll(employees)
        val ids = dao.getAllEmployeeIds()
        assertTrue(ids.containsAll(listOf(1L, 2L)))
    }

    @Test
    fun testGetEmployeesByQuery() = runTest {
        val employees = listOf(
            com.example.sheetcompute.data.entities.EmployeeEntity(1L, "Employee 1"),
            com.example.sheetcompute.data.entities.EmployeeEntity(2L, "Employee 2"),
            com.example.sheetcompute.data.entities.EmployeeEntity(3L, "Employee 3")
        )
        dao.insertAll(employees)
        val result = dao.getEmployees("Employee 2")
        assertEquals(1, result.size)
        assertEquals("Employee 2", result[0].name)
        val all = dao.getEmployees("")
        assertEquals(3, all.size)
    }
}