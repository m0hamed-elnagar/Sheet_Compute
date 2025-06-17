package com.example.sheetcompute.data.roomDB

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sheetcompute.data.daos.AttendanceDao
import com.example.sheetcompute.data.daos.EmployeeDao
import com.example.sheetcompute.data.daos.HolidayDao
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.domain.DomainIntegration

@Database(
    entities = [AttendanceRecord::class, Holiday::class,EmployeeEntity::class],
    version = 2,
//    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    //    abstract fun employeeAttendanceDao(): EmployeeAttendanceDao
    abstract fun holidayDao(): HolidayDao
    abstract fun EmployeeAttendanceDao() :AttendanceDao
    abstract fun employeeDao(): EmployeeDao

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