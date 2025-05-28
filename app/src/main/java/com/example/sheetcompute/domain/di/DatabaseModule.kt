//package com.example.sheetcompute.domain.di
//
//import android.content.Context
//import android.content.SharedPreferences
//import com.example.sheetcompute.data.local.daos.HolidayDao
//import com.example.sheetcompute.data.local.roomDB.AppDatabase
//import com.example.sheetcompute.domain.repo.HolidayRepositoryImpl
//import com.example.sheetcompute.domain.repo.HolidayRepository
//import dagger.Binds
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object DatabaseModule {
//
//    @Provides
//    @Singleton
//    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
//        return AppDatabase.Companion.get(context)
//    }
//
//    @Provides
//    fun provideHolidayDao(database: AppDatabase): HolidayDao {
//        return database.holidayDao()
//    }
//    private const val SHARED_PREFERENCE_NAME = "sheet_compute_prefs"
//
//    @Provides
//    @Singleton
//    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
//        return context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
//    }
//
//
//}