package com.sfedu.degree_android.ui.screens.map

data class MapCameraState(
    val lat: Double,
    val lon: Double,
    val zoom: Float,
    val azimuth: Float,
    val tilt: Float
)