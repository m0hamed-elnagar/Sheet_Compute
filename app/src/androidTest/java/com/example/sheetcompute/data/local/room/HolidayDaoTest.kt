package com.example.sheetcompute.data.local.room

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.sheetcompute.data.entities.Holiday
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
// This class is a placeholder for the AppDatabase tests.
class HolidayDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var dao: HolidayDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room
            .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.holidayDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testInsertAndGetHoliday() = runTest {
        // Given a holiday to insert
        val holiday = Holiday(
            id = 1,
            startDate = LocalDate.of(2024, 7, 1),
            endDate = LocalDate.of(2024, 7, 1),
            name = "Christmas Holiday",
            note = "Public holiday for Christmas"
        )

        // When inserting the holiday
        dao.addHoliday(holiday)

        // Then it should be retrievable
        val retrievedHoliday = dao.getHolidaysByDateRange(
            startDate = LocalDate.of(2024, 7, 1),
            endDate = LocalDate.of(2024, 7, 1)
        ).firstOrNull()
        assertNotNull(retrievedHoliday)
        assertEquals(holiday.name, retrievedHoliday?.name)
        assertEquals(holiday.startDate, retrievedHoliday?.startDate)


    }

    @Test
    fun testUpdateHoliday() = runTest {
        val holiday = Holiday(
            id = 2,
            startDate = LocalDate.of(2024, 8, 1),
            endDate = LocalDate.of(2024, 8, 1),
            name = "Eid Holiday",
            note = "Public holiday for Eid"
        )
        dao.addHoliday(holiday)
        val updatedHoliday = holiday.copy(name = "Eid al-Fitr Holiday")
        dao.updateHoliday(updatedHoliday)
        val retrieved = dao.getHolidaysByDateRange(
            startDate = LocalDate.of(2024, 8, 1),
            endDate = LocalDate.of(2024, 8, 1)
        ).firstOrNull()
        assertNotNull(retrieved)
        assertEquals("Eid al-Fitr Holiday", retrieved?.name)
    }

    @Test
    fun testDeleteHoliday() = runTest {
        val holiday = Holiday(
            id = 3,
            startDate = LocalDate.of(2024, 9, 1),
            endDate = LocalDate.of(2024, 9, 1),
            name = "Labor Day",
            note = "Labor Day holiday"
        )
        dao.addHoliday(holiday)
        dao.deleteHoliday(holiday)
        val holidays = dao.getHolidaysByDateRange(
            startDate = LocalDate.of(2024, 9, 1),
            endDate = LocalDate.of(2024, 9, 1)
        )
        assertTrue(holidays.isEmpty())
    }

    @Test
    fun testGetHolidayRanges() = runTest {
        val holiday1 = Holiday(
            id = 4,
            startDate = LocalDate.of(2024, 10, 1),
            endDate = LocalDate.of(2024, 10, 3),
            name = "Autumn Break",
            note = "Short break"
        )
        val holiday2 = Holiday(
            id = 5,
            startDate = LocalDate.of(2024, 10, 5),
            endDate = LocalDate.of(2024, 10, 7),
            name = "Midterm Break",
            note = "Midterm break"
        )
        dao.addHoliday(holiday1)
        dao.addHoliday(holiday2)
        val ranges = dao.getHolidayRanges(
            start = LocalDate.of(2024, 10, 1),
            end = LocalDate.of(2024, 10, 10)
        )
        assertEquals(2, ranges.size)
        assertEquals(LocalDate.of(2024, 10, 1), ranges[0].startDate)
        assertEquals(LocalDate.of(2024, 10, 3), ranges[0].endDate)
        assertEquals(LocalDate.of(2024, 10, 5), ranges[1].startDate)
        assertEquals(LocalDate.of(2024, 10, 7), ranges[1].endDate)
    }

}