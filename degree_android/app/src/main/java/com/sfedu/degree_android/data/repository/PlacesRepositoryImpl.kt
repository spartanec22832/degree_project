package com.sfedu.degree_android.data.repository

import com.sfedu.degree_android.data.local.dao.PlaceCacheDao
import com.sfedu.degree_android.data.mapper.toCachedEntity
import com.sfedu.degree_android.data.mapper.toDto
import com.sfedu.degree_android.data.mapper.toSearchDto
import com.sfedu.degree_android.data.remote.api.PlacesApi
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.data.remote.dto.PlaceMapDto
import com.sfedu.degree_android.data.remote.dto.PlaceSearchDto
import com.sfedu.degree_android.domain.repository.PlacesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class PlacesRepositoryImpl @Inject constructor(
    private val api: PlacesApi,
    private val cacheDao: PlaceCacheDao
) : PlacesRepository {

    private var isWarmedUp = false

    override suspend fun getMapPlaces(): List<PlaceMapDto> {
        return try {
            val remote = api.getMapPlaces()
            cacheDao.replaceAllMapPlaces(remote.map { it.toCachedEntity() })
            remote
        } catch (e: Exception) {
            val cached = cacheDao.getAllMapPlaces().map { it.toDto() }
            if (cached.isNotEmpty()) cached else throw e
        }
    }

    override suspend fun getPlace(id: Int): PlaceDto {
        return try {
            val remote = api.getPlace(id)
            cacheDao.upsertPlace(remote.toCachedEntity())
            cacheDao.replacePhotosForPlace(
                placeId = id,
                items = remote.photos.map { it.toCachedEntity(id) }
            )
            remote
        } catch (e: Exception) {
            val cachedPlace = cacheDao.getPlaceById(id)
            if (cachedPlace != null) {
                val cachedPhotos = cacheDao.getPhotosByPlaceId(id).map { it.toDto() }
                cachedPlace.toDto(cachedPhotos)
            } else {
                throw e
            }
        }
    }

    override suspend fun searchPlaces(query: String): List<PlaceSearchDto> {
        return try {
            api.searchPlaces(query)
        } catch (e: Exception) {
            cacheDao.searchPlaces(query.trim()).map { it.toSearchDto() }
        }
    }

    override suspend fun warmUpPlacesCache(placeIds: List<Int>) {
        if (isWarmedUp) return

        isWarmedUp = true
        coroutineScope {
            placeIds.chunked(5).forEach { chunk ->
                chunk.map { id ->
                    async {
                        runCatching {
                            val remote = api.getPlace(id)
                            cacheDao.upsertPlace(remote.toCachedEntity())
                            cacheDao.replacePhotosForPlace(
                                placeId = id,
                                items = remote.photos.map { it.toCachedEntity(id) }
                            )
                        }
                    }
                }.awaitAll()
            }
        }
    }
}