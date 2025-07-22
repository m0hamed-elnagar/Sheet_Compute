package com.example.sheetcompute.domain.di
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.sheetcompute.data.entities.SHARED_PREFERENCE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
}
