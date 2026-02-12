package com.sfedu.degree_android.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sfedu.degree_android.R
import com.sfedu.degree_android.core.network.ApiConstants
import com.sfedu.degree_android.ui.screens.favorites.FavoritesStateViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    mapStateVm: MapStateViewModel,
    onOpenDetails: (placeId: Int) -> Unit = {} ,
    token: String?
) {
    val context = LocalContext.current
    val isAuthed = !token.isNullOrBlank()
    val vm: MapViewModel = hiltViewModel()
    val state = vm.state

    val previewVm: PlacePreviewViewModel = hiltViewModel()
    val previewState = previewVm.state

    var selectedPlaceId by remember { mutableStateOf<Int?>(null) }
    var currentLocationPoint by remember { mutableStateOf<Point?>(null) }

    val favVm: FavoritesStateViewModel = hiltViewModel()
    val favoriteIds by favVm.favoriteIds.collectAsState()

    val isFavoriteForSelected =
        isAuthed && selectedPlaceId != null && favoriteIds.contains(selectedPlaceId)

    LaunchedEffect(isAuthed) {
        if (isAuthed) favVm.refresh()
    }

    val onFavoriteClick: (Int) -> Unit = { id ->
        if (!isAuthed) toast(
            context = context,
            msg = "Избранное недоступно, вы не авторизованы"
        )
        else favVm.toggle(id)
    }

    val mapView = remember {
        MapView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    // фильтрация
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val placesByType = remember(state.places, state.enabledTypes) {
        state.places.filter { p ->
            when {
                p.type.isFoodType() -> state.enabledTypes.contains(MapPlaceType.FOOD)
                p.type.isPlacesType() -> state.enabledTypes.contains(MapPlaceType.PLACES)
                p.type.isHotelType() -> state.enabledTypes.contains(MapPlaceType.HOTELS)
                else -> true
            }
        }
    }

    val filteredPlaces = remember(
        placesByType,
        state.bufferEnabled,
        state.bufferRadiusMeters,
        currentLocationPoint
    ) {
        if (!state.bufferEnabled) {
            placesByType
        } else {
            val center = currentLocationPoint
            if (center == null) {
                // буфер включен, но локации нет — пока ничего не режем
                placesByType
            } else {
                placesByType.filter { p ->
                    distanceMeters(
                        lat1 = center.latitude,
                        lon1 = center.longitude,
                        lat2 = p.latitude,
                        lon2 = p.longitude
                    ) <= state.bufferRadiusMeters
                }
            }
        }
    }

    // Коллекция для маркеров
    val markersCollection: MapObjectCollection = remember {
        mapView.mapWindow.map.mapObjects.addCollection()
    }

    // Иконки маркеров (vector -> bitmap)
    val placeProvider = remember(context) {
        imageProviderFromVector(context, R.drawable.ic_marker_place, 36)
    }
    val foodProvider = remember(context) {
        imageProviderFromVector(context, R.drawable.ic_marker_food, 36)
    }
    val hotelProvider = remember(context) {
        imageProviderFromVector(context, R.drawable.ic_marker_hotel, 36)
    }

    // Коллекция для буферного круга
    val bufferCollection: MapObjectCollection = remember {
        mapView.mapWindow.map.mapObjects.addCollection()
    }

    // Центр Таганрога по умолчанию
    val taganrogCenter = remember { Point(47.23617, 38.89688) }
    val initialZoom = 13.5f

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

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

    // Один "вечный" listener для всех плацемарков (иначе GC может их финализировать)
    val placemarkTapListener = remember(previewVm) {
        MapObjectTapListener { mapObject, _ ->
            val id = mapObject.userData as? Int ?: return@MapObjectTapListener false
            selectedPlaceId = id
            previewVm.clear()
            previewVm.load(id)
            true
        }
    }

    // Слушатель камеры: сохраняем позицию для возврата "назад" на ту же точку/масштаб
    val cameraListener = remember(mapStateVm) {
        CameraListener { _: Map, cameraPosition: CameraPosition, _: Any, finished: Boolean ->
            // сохраняем в конце жеста, чтобы не спамить SavedStateHandle
            if (finished) {
                mapStateVm.saveCamera(
                    MapCameraState(
                        lat = cameraPosition.target.latitude,
                        lon = cameraPosition.target.longitude,
                        zoom = cameraPosition.zoom,
                        azimuth = cameraPosition.azimuth,
                        tilt = cameraPosition.tilt
                    )
                )
            }
        }
    }

    // Инициализация камеры (restore если есть) + загрузка маркеров + пермишены
    LaunchedEffect(Unit) {
        val map = mapView.mapWindow.map

        val saved = mapStateVm.getCameraOrNull()
        if (saved != null) {
            map.move(
                CameraPosition(
                    Point(saved.lat, saved.lon),
                    saved.zoom,
                    saved.azimuth,
                    saved.tilt
                ),
                Animation(Animation.Type.SMOOTH, 0.35f),
                null
            )
        } else {
            map.move(
                CameraPosition(taganrogCenter, initialZoom, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0.6f),
                null
            )
        }

        vm.load()

        if (hasLocationPermission()) {
            userLocationLayer.isVisible = true
            userLocationLayer.isHeadingModeActive = true
            currentLocationPoint = getLastKnownPoint(context)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(state.bufferEnabled, state.bufferRadiusMeters, currentLocationPoint) {
        bufferCollection.clear()

        if (!state.bufferEnabled) return@LaunchedEffect
        val center = currentLocationPoint ?: return@LaunchedEffect

        val circleObj = bufferCollection.addCircle(
            Circle(center, state.bufferRadiusMeters)
        )
        circleObj.fillColor = 0x332196F3.toInt()    // полупрозрачная заливка
        circleObj.strokeColor = 0xAA2196F3.toInt()  // обводка
        circleObj.strokeWidth = 2.5f
    }

    // Перерисовка маркеров по данным с бэка
    LaunchedEffect(filteredPlaces) {
        markersCollection.clear()

        filteredPlaces.forEach { p ->
            val provider = markerProviderByType(
                type = p.type,
                placeProvider = placeProvider,
                foodProvider = foodProvider,
                hotelProvider = hotelProvider
            )

            val placemark = markersCollection.addPlacemark(
                Point(p.latitude, p.longitude),
                provider
            )

            placemark.userData = p.id
            placemark.addTapListener(placemarkTapListener)
        }
    }

    // Lifecycle + LocationManager + CameraListener + MapKitFactory lifecycle
    DisposableEffect(Unit) {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()

        val map = mapView.mapWindow.map
        map.addCameraListener(cameraListener)

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                currentLocationPoint = Point(location.latitude, location.longitude)
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
            override fun onProviderEnabled(provider: String) = Unit
            override fun onProviderDisabled(provider: String) = Unit
        }

        if (hasLocationPermission()) {
            try { lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 5f, locListener) } catch (_: Exception) {}
            try { lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L, 5f, locListener) } catch (_: Exception) {}
            currentLocationPoint = currentLocationPoint ?: getLastKnownPoint(context)
        }

        onDispose {
            try { lm.removeUpdates(locListener) } catch (_: Exception) {}
            map.removeCameraListener(cameraListener)

            mapView.onStop()
            MapKitFactory.getInstance().onStop()
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.currentValue == DrawerValue.Open,
        drawerContent = {
            ModalDrawerSheet {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.close() } }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Закрыть меню"
                        )
                    }

                    Text(
                        text = "Управление картой",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(
                    text = "Фильтры",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                FilterCheckRow(
                    title = MapPlaceType.FOOD.title,
                    checked = state.enabledTypes.contains(MapPlaceType.FOOD),
                    onToggle = { vm.toggleType(MapPlaceType.FOOD) }
                )

                FilterCheckRow(
                    title = MapPlaceType.PLACES.title,
                    checked = state.enabledTypes.contains(MapPlaceType.PLACES),
                    onToggle = { vm.toggleType(MapPlaceType.PLACES) }
                )

                FilterCheckRow(
                    title = MapPlaceType.HOTELS.title,
                    checked = state.enabledTypes.contains(MapPlaceType.HOTELS),
                    onToggle = { vm.toggleType(MapPlaceType.HOTELS) }
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Буферная зона",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.bufferEnabled) "Включена" else "Выключена",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = state.bufferEnabled,
                        onCheckedChange = { vm.setBufferEnabled(it) }
                    )
                }

                val focusManager = LocalFocusManager.current
                var radiusText by remember { mutableStateOf(state.bufferRadiusMeters.toInt().toString()) }

                LaunchedEffect(state.bufferRadiusMeters) {
                    val newText = state.bufferRadiusMeters.toInt().toString()
                    if (radiusText != newText) radiusText = newText
                }

                Text(
                    text = "Радиус (м)",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp)
                )

                OutlinedTextField(
                    value = radiusText,
                    onValueChange = { newValue ->
                        // только цифры
                        val digitsOnly = newValue.filter { it.isDigit() }
                        radiusText = digitsOnly

                        val asInt = digitsOnly.toIntOrNull()
                        if (asInt != null) {
                            vm.setBufferRadiusMeters(asInt.toFloat())
                        }
                    },
                    enabled = state.bufferEnabled,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            // если пользователь оставил пусто, то вернём актуальное из state
                            if (radiusText.isBlank()) {
                                radiusText = state.bufferRadiusMeters.toInt().toString()
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Slider(
                    value = state.bufferRadiusMeters,
                    onValueChange = { vm.setBufferRadiusMeters(it) },
                    valueRange = 100f..5000f,
                    enabled = state.bufferEnabled,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(12.dp))
            }
        }
    ) {
        Box(modifier = modifier) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView }
            )

            // ☰ Кнопка меню слева сверху
            Surface(
                color = Color.White,
                contentColor = Color.Black,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 16.dp)
                    .size(44.dp)
            ) {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Меню",
                        tint = Color.Black
                    )
                }
            }

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

            // ---------- ПРЕДПРОСМОТР ----------
            AnimatedVisibility(
                visible = selectedPlaceId != null,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = contentPadding.calculateBottomPadding() + 12.dp
                        )
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        tonalElevation = 4.dp,
                        shadowElevation = 10.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val place = previewState.place

                        Column {
                            val rawPath =
                                place?.photos?.firstOrNull { it.isMain }?.url
                                    ?: place?.photos?.firstOrNull()?.url

                            val photoUrl = rawPath?.let { toAbsoluteUrl(ApiConstants.BASE_URL, it) }

                            if (photoUrl != null) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                )
                            }

                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val pid = selectedPlaceId

                                    IconButton(
                                        onClick = { pid?.let { onFavoriteClick(it) } },
                                        enabled = pid != null
                                    ) {
                                        Icon(
                                            imageVector = if (isFavoriteForSelected) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                            contentDescription = "Избранное",
                                            tint = if (isFavoriteForSelected) MaterialTheme.colorScheme.error else LocalContentColor.current
                                        )
                                    }
                                    Text(
                                        text = place?.name ?: "",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            selectedPlaceId = null
                                            previewVm.clear()
                                        }
                                    ) {
                                        Icon(Icons.Filled.Close, contentDescription = "Закрыть")
                                    }
                                }

                                when {
                                    place == null && previewState.loading -> {
                                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                        Spacer(Modifier.height(8.dp))
                                        Text("Загрузка...")
                                    }

                                    previewState.error != null && place == null -> {
                                        Text("Ошибка: ${previewState.error}")
                                    }

                                    place != null -> {
                                        Spacer(Modifier.height(4.dp))

                                        if (previewState.loading) {
                                            LinearProgressIndicator(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 6.dp)
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            val ratingAvg = place.averageRating ?: 0.0

                                            RatingPickerSmall(
                                                value = previewVm.myRating,
                                                enabled = !previewVm.ratingSubmitting,
                                                onChange = { newValue ->
                                                    val id = selectedPlaceId ?: return@RatingPickerSmall

                                                    if (!isAuthed) {
                                                        toast(context, "Оценка недоступна, вы не авторизованы")
                                                        return@RatingPickerSmall
                                                    }

                                                    previewVm.setRating(id, newValue) { msg ->
                                                        toast(context, msg)
                                                    }
                                                }
                                            )

                                            Text(text = String.format("%.1f", ratingAvg), fontSize = 13.sp)

                                            place.worktime?.takeIf { it.isNotBlank() }?.let {
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    text = it,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }

                                        place.description?.takeIf { it.isNotBlank() }?.let {
                                            Spacer(Modifier.height(8.dp))
                                            Text(
                                                text = it,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val id = selectedPlaceId ?: return@Button
                            onOpenDetails(id)
                        },
                        enabled = previewState.place != null && !previewState.loading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ПОДРОБНЕЕ", fontWeight = FontWeight.SemiBold)
                    }
                }
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

@Composable
private fun RatingPickerSmall(
    value: Int,
    enabled: Boolean,
    onChange: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..5) {
            val filled = i <= value
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = "Оценка $i",
                modifier = Modifier
                    .size(18.dp)
                    .clickable(enabled = enabled) { onChange(i) },
                tint = if (enabled) LocalContentColor.current
                else LocalContentColor.current.copy(alpha = 0.5f)
            )
        }
    }
}

private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val out = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, out)
    return out[0]
}

@Composable
private fun FilterCheckRow(
    title: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(
                width = 1.dp,
                color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            ),
            color = if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent,
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (checked) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun String.isFoodType(): Boolean {
    val t = trim().lowercase()
    return t == "еда" || t == "food"
}

private fun String.isPlacesType(): Boolean {
    val t = trim().lowercase()
    return t == "места" || t == "place" || t == "places"
}

private fun String.isHotelType(): Boolean {
    val t = trim().lowercase()
    return t == "гостиницы" || t == "hotel" || t == "hotels"
}

private fun markerProviderByType(
    type: String,
    placeProvider: ImageProvider,
    foodProvider: ImageProvider,
    hotelProvider: ImageProvider
): ImageProvider {
    val t = type.trim().lowercase()
    return when (t) {
        "места", "place", "places" -> placeProvider
        "еда", "food" -> foodProvider
        "гостиницы", "hotel", "hotels" -> hotelProvider
        else -> placeProvider
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

private fun imageProviderFromVector(
    context: Context,
    @DrawableRes resId: Int,
    sizeDp: Int
): ImageProvider {
    val drawable = AppCompatResources.getDrawable(context, resId)
        ?: error("Drawable $resId not found")

    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt().coerceAtLeast(1)

    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return ImageProvider.fromBitmap(bitmap)
}

private fun toAbsoluteUrl(baseUrl: String, pathOrUrl: String): String {
    if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) return pathOrUrl
    val baseFixed = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
    val pathFixed = if (pathOrUrl.startsWith("/")) pathOrUrl else "/$pathOrUrl"
    return baseFixed + pathFixed
}

private fun toast(context: Context, msg: String) =
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

