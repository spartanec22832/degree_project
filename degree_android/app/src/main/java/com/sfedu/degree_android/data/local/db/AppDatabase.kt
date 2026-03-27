package com.sfedu.degree_android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sfedu.degree_android.data.local.dao.PlaceCacheDao
import com.sfedu.degree_android.data.local.entity.CachedMapPlaceEntity
import com.sfedu.degree_android.data.local.entity.CachedPhotoEntity
import com.sfedu.degree_android.data.local.entity.CachedPlaceEntity

@Database(
    entities = [
        CachedMapPlaceEntity::class,
        CachedPlaceEntity::class,
        CachedPhotoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placeCacheDao(): PlaceCacheDao
}