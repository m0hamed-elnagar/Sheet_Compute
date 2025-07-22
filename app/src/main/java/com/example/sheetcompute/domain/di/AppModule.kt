package com.example.sheetcompute.domain.di

import android.app.Application
import com.example.sheetcompute.App
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideApplication(app: Application): App {
        return app as App
    }
}
