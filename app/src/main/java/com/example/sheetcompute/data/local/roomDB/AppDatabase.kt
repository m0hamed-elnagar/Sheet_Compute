package com.example.sheetcompute.data.local.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sheetcompute.data.local.daos.HolidayDao
import com.example.sheetcompute.data.local.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.data.local.entities.Holiday
import com.example.sheetcompute.domain.DomainIntegration

@Database(
    entities = [EmployeeAttendanceRecord::class, Holiday::class],
    version = 2,
//    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    //    abstract fun employeeAttendanceDao(): EmployeeAttendanceDao
    abstract fun holidayDao(): HolidayDao

    companion object {
        @Volatile
        private lateinit var INSTANCE: AppDatabase

        fun get(): AppDatabase = synchronized(this) {
            if (!Companion::INSTANCE.isInitialized)
                INSTANCE = Room
                    .databaseBuilder(
                        DomainIntegration.getApplication(),
                        AppDatabase::class.java,
                        "attendance_holidays.db"
                    )
                    .fallbackToDestructiveMigration(false)
                    .build()

            INSTANCE
        }
    }
}