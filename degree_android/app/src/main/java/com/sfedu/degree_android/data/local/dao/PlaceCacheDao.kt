package com.sfedu.degree_android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sfedu.degree_android.data.local.entity.CachedMapPlaceEntity
import com.sfedu.degree_android.data.local.entity.CachedPhotoEntity
import com.sfedu.degree_android.data.local.entity.CachedPlaceEntity

@Dao
interface PlaceCacheDao {

    @Query("SELECT * FROM cached_map_places")
    suspend fun getAllMapPlaces(): List<CachedMapPlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapPlaces(items: List<CachedMapPlaceEntity>)

    @Query("DELETE FROM cached_map_places")
    suspend fun clearMapPlaces()

    suspend fun replaceAllMapPlaces(items: List<CachedMapPlaceEntity>) {
        clearMapPlaces()
        insertMapPlaces(items)
    }

    @Query("SELECT * FROM cached_places WHERE id = :id LIMIT 1")
    suspend fun getPlaceById(id: Int): CachedPlaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlace(place: CachedPlaceEntity)

    @Query("SELECT * FROM cached_photos WHERE placeId = :placeId ORDER BY isMain DESC, id ASC")
    suspend fun getPhotosByPlaceId(placeId: Int): List<CachedPhotoEntity>

    @Query("DELETE FROM cached_photos WHERE placeId = :placeId")
    suspend fun clearPhotosByPlaceId(placeId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(items: List<CachedPhotoEntity>)

    suspend fun replacePhotosForPlace(placeId: Int, items: List<CachedPhotoEntity>) {
        clearPhotosByPlaceId(placeId)
        insertPhotos(items)
    }

    @Query(
        """
        SELECT * FROM cached_places
        WHERE name LIKE '%' || :query || '%'
           OR type LIKE '%' || :query || '%'
           OR category LIKE '%' || :query || '%'
        ORDER BY name
        LIMIT 20
        """
    )
    suspend fun searchPlaces(query: String): List<CachedPlaceEntity>
}