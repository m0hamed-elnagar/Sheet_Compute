//package com.example.sheetcompute.domain.di
//
//import com.example.sheetcompute.domain.repo.HolidayRepository
//import com.example.sheetcompute.domain.repo.HolidayRepositoryImpl
//import dagger.Binds
//import dagger.Module
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//abstract class RepositoryModule {
//
//    @Binds
//    @Singleton
//    abstract fun bindHolidayRepository(
//        holidayRepositoryImpl: HolidayRepositoryImpl
//    ): HolidayRepository
//}