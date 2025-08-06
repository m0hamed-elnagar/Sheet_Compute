package com.example.sheetcompute.domain.di

import android.app.Application
import androidx.room.Room
import com.example.sheetcompute.data.local.room.AppDatabase
import com.example.sheetcompute.data.local.room.daos.AttendanceDao
import com.example.sheetcompute.data.local.room.daos.EmployeeDao
import com.example.sheetcompute.data.local.room.daos.HolidayDao
import com.example.sheetcompute.data.repo.HolidayRepo
import com.example.sheetcompute.data.repo.HolidayRepoInterface
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "attendance_holidays.db"
        ).fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideHolidayDao(db: AppDatabase): HolidayDao = db.holidayDao()
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class HolidayRepoModule {

        @Binds
        abstract fun bindHolidayRepo(
            impl: HolidayRepo
        ): HolidayRepoInterface
    }
    @Provides
    fun provideAttendanceDao(db: AppDatabase): AttendanceDao = db.attendanceDao()

    @Provides
    fun provideEmployeeDao(db: AppDatabase): EmployeeDao = db.employeeDao()
}


