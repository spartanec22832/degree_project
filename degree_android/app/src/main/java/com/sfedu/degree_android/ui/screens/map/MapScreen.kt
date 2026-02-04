package com.sfedu.degree_android.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    // Центр Таганрога (lat, lon)
    val taganrogCenter = remember { Point(47.23617, 38.89688) }
    val initialZoom = 13.5f

    // Актуальная позиция пользователя (для кнопки "к себе")
    var currentLocationPoint by remember { mutableStateOf<Point?>(null) }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    // Слой пользователя MapKit (стрелка/точка)
    val userLocationLayer: UserLocationLayer = remember {
        MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            if (hasLocationPermission()) {
                userLocationLayer.isVisible = true
                userLocationLayer.isHeadingModeActive = true
                currentLocationPoint = getLastKnownPoint(context)
            }
        }
    )

    // Один раз: ставим Таганрог и, если есть разрешения, подтягиваем last known
    LaunchedEffect(Unit) {
        val map = mapView.mapWindow.map
        map.move(
            CameraPosition(taganrogCenter, initialZoom, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0.6f),
            null
        )

        if (hasLocationPermission()) {
            userLocationLayer.isVisible = true
            userLocationLayer.isHeadingModeActive = true
            currentLocationPoint = getLastKnownPoint(context)
        } else {
            // просим разрешение сразу, чтобы точка пользователя и кнопка работали
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Lifecycle MapView + подписка на обновления LocationManager
    DisposableEffect(Unit) {
        mapView.onStart()

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                currentLocationPoint = Point(location.latitude, location.longitude)
            }
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
            override fun onProviderEnabled(provider: String) = Unit
            override fun onProviderDisabled(provider: String) = Unit
        }

        if (hasLocationPermission()) {
            try { lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 5f, listener) } catch (_: Exception) {}
            try { lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L, 5f, listener) } catch (_: Exception) {}
            currentLocationPoint = currentLocationPoint ?: getLastKnownPoint(context)
        }

        onDispose {
            try { lm.removeUpdates(listener) } catch (_: Exception) {}
            mapView.onStop()
        }
    }

    fun zoomBy(delta: Float) {
        val map = mapView.mapWindow.map
        val current = map.cameraPosition
        val newZoom = (current.zoom + delta).coerceIn(2f, 20f)
        map.move(
            CameraPosition(current.target, newZoom, current.azimuth, current.tilt),
            Animation(Animation.Type.SMOOTH, 0.25f),
            null
        )
    }

    fun moveToMyLocation() {
        if (!hasLocationPermission()) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        val point = currentLocationPoint ?: getLastKnownPoint(context)
        if (point != null) {
            currentLocationPoint = point
            val map = mapView.mapWindow.map
            val zoom = maxOf(map.cameraPosition.zoom, 16f)
            map.move(
                CameraPosition(point, zoom, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0.4f),
                null
            )
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )

        // Zoom кнопки справа по центру
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ZoomSquareButton(
                icon = Icons.Filled.Add,
                contentDescription = "Увеличить",
                onClick = { zoomBy(+1f) }
            )
            ZoomSquareButton(
                icon = Icons.Filled.Remove,
                contentDescription = "Уменьшить",
                onClick = { zoomBy(-1f) }
            )
        }

        // Кнопка "моя геопозиция" справа снизу над bottom bar
        Surface(
            color = Color.White,
            contentColor = Color.Black,
            shape = RoundedCornerShape(999.dp),
            shadowElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 16.dp,
                    bottom = contentPadding.calculateBottomPadding() + 16.dp
                )
                .size(50.dp)
        ) {
            IconButton(onClick = { moveToMyLocation() }) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = "Моя геопозиция",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
private fun ZoomSquareButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White,
        contentColor = Color.Black,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 6.dp,
        modifier = Modifier.size(44.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = contentDescription, tint = Color.Black)
        }
    }
}

private fun getLastKnownPoint(context: Context): Point? {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    if (!fine && !coarse) return null

    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER,
        LocationManager.PASSIVE_PROVIDER
    )

    for (p in providers) {
        try {
            val loc = lm.getLastKnownLocation(p) ?: continue
            return Point(loc.latitude, loc.longitude)
        } catch (_: Exception) {}
    }
    return null
}
