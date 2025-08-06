package com.example.sheetcompute.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.data.local.room.daos.AttendanceDao
import com.example.sheetcompute.data.local.room.daos.EmployeeDao
import com.example.sheetcompute.data.local.room.daos.HolidayDao

@Database(
    entities = [AttendanceRecord::class, Holiday::class, EmployeeEntity::class],
    version = 2,
//    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun holidayDao(): HolidayDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun employeeDao(): EmployeeDao

}