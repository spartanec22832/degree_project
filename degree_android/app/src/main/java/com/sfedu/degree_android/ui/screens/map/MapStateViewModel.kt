package com.sfedu.degree_android.ui.screens.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapStateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        const val K_LAT = "map_lat"
        const val K_LON = "map_lon"
        const val K_ZOOM = "map_zoom"
        const val K_AZIMUTH = "map_azimuth"
        const val K_TILT = "map_tilt"
    }

    fun saveCamera(state: MapCameraState) {
        savedStateHandle[K_LAT] = state.lat
        savedStateHandle[K_LON] = state.lon
        savedStateHandle[K_ZOOM] = state.zoom
        savedStateHandle[K_AZIMUTH] = state.azimuth
        savedStateHandle[K_TILT] = state.tilt
    }

    fun getCameraOrNull(): MapCameraState? {
        val lat = savedStateHandle.get<Double>(K_LAT) ?: return null
        val lon = savedStateHandle.get<Double>(K_LON) ?: return null
        val zoom = savedStateHandle.get<Float>(K_ZOOM) ?: return null
        val azimuth = savedStateHandle.get<Float>(K_AZIMUTH) ?: 0f
        val tilt = savedStateHandle.get<Float>(K_TILT) ?: 0f
        return MapCameraState(lat, lon, zoom, azimuth, tilt)
    }
}
