package com.sfedu.degree_android.core.di

import android.content.Context
import androidx.room.Room
import com.sfedu.degree_android.data.local.db.AppDatabase
import com.sfedu.degree_android.data.local.dao.PlaceCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "degree_cache.db"
        ).build()

    @Provides
    @Singleton
    fun providePlaceCacheDao(db: AppDatabase): PlaceCacheDao =
        db.placeCacheDao()
}