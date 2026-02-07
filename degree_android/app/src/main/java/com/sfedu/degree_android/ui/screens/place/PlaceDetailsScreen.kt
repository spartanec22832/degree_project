package com.sfedu.degree_android.ui.screens.place

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sfedu.degree_android.core.network.ApiConstants
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.ui.screens.favorites.FavoritesStateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailsScreen(
    placeId: Int,
    token: String?,
    onBack: () -> Unit
) {
    val vm: PlaceDetailsViewModel = hiltViewModel()
    val state = vm.state

    val context = LocalContext.current
    val isAuthed = !token.isNullOrBlank()
    val favVm: FavoritesStateViewModel = hiltViewModel()

    val favoriteIds by favVm.favoriteIds.collectAsState()
    val isFav = isAuthed && favoriteIds.contains(placeId)

    LaunchedEffect(isAuthed) {
        if (isAuthed) favVm.refresh()
    }

    val userRating = vm.myRating

    LaunchedEffect(placeId) { vm.load(placeId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.place?.name ?: "Подробнее",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val id = state.place?.id ?: return@IconButton
                            if (!isAuthed) {
                                Toast.makeText(
                                    context,
                                    "Избранное недоступно, вы не авторизованы",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                favVm.toggle(id)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Избранное",
                            tint = if (isFav) MaterialTheme.colorScheme.error else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.place == null && state.loading -> {
                    CircularProgressIndicator(Modifier.padding(24.dp))
                }

                state.error != null && state.place == null -> {
                    Text("Ошибка: ${state.error}", modifier = Modifier.padding(16.dp))
                }

                state.place != null -> {
                    val place = state.place!!

                    if (state.loading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    val photos = place.photos
                    val pagerState = rememberPagerState(pageCount = { photos.size.coerceAtLeast(1) })

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // --- Фото (как у тебя, не трогаем) ---
                        item {
                            if (photos.isNotEmpty()) {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                ) { page ->
                                    val url = toAbsoluteUrl(ApiConstants.BASE_URL, photos[page].url)
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            }
                        }

                        // --- Контент под фотками ---
                        item {
                            Column(Modifier.padding(16.dp)) {

                                // Название + звездочки справа (UI под выставление рейтинга)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = place.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        RatingPicker(
                                            value = userRating,
                                            enabled = !vm.ratingSubmitting,
                                            onChange = { newValue ->
                                                if (!isAuthed) {
                                                    Toast.makeText(context, "Оценка недоступна, вы не авторизованы", Toast.LENGTH_SHORT).show()
                                                    return@RatingPicker
                                                }
                                                vm.setRating(placeId, newValue) { msg ->
                                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        )

                                        // рейтинг от бэка
                                        val avg = place.averageRating ?: 0.0
                                        Text(
                                            text = String.format("%.1f", avg),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                // Описание
                                place.description?.takeIf { it.isNotBlank() }?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(Modifier.height(14.dp))
                                }

                                // Блоки доп инфы в стиле скрина 2
                                InfoSection(place = place)

                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingPicker(
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
                    .size(22.dp)
                    .clickable(enabled = enabled) { onChange(i) },
                tint = if (enabled) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun InfoSection(place: PlaceDto) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Адрес
        place.address?.takeIf { it.isNotBlank() }?.let { addr ->
            InfoCard(
                icon = Icons.Filled.LocationOn,
                title = "Адрес",
                body = addr,
                actionText = "Показать на карте",
                onAction = {
                    // Заглушка: позже сделаем возврат на карту + центрирование на объект
                }
            )
        }

        // Стоимость
        place.price?.let { price ->
            InfoCard(
                icon = Icons.Filled.TravelExplore,
                title = "Стоимость",
                body = "от $price ₽"
            )
        }

        // Часы работы
        place.worktime?.takeIf { it.isNotBlank() }?.let { wt ->
            InfoCard(
                icon = Icons.Filled.Schedule,
                title = "Часы работы",
                body = wt
            )
        }

        // Контакты (у тебя может быть phone/link; если добавишь email — тоже покажем)
        val contacts = buildList<String> {
            place.contactLink?.takeIf { it.isNotBlank() }?.let { add(it) }
            place.contactPhone?.takeIf { it.isNotBlank() }?.let { add(it) }
        }

        if (contacts.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Контакты", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(10.dp))

                    contacts.forEach { c ->
                        ContactLine(text = c)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))
            Text(body)

            if (actionText != null && onAction != null) {
                Spacer(Modifier.height(10.dp))
                TextButton(
                    onClick = onAction,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(actionText)
                }
            }
        }
    }
}

@Composable
private fun ContactLine(text: String) {
    val icon = if (text.startsWith("http")) Icons.Filled.Link else Icons.Filled.Phone
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text)
    }
}

// Важно: пусть будет top-level (без private), чтобы не ловить ошибки со scope
fun toAbsoluteUrl(baseUrl: String, pathOrUrl: String): String {
    if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) return pathOrUrl
    val baseFixed = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
    val pathFixed = if (pathOrUrl.startsWith("/")) pathOrUrl else "/$pathOrUrl"
    return baseFixed + pathFixed
}
