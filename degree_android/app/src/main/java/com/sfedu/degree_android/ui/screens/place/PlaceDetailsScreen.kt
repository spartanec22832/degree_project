package com.sfedu.degree_android.ui.screens.place

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sfedu.degree_android.core.network.ApiConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailsScreen(
    placeId: Int,
    onBack: () -> Unit
) {
    val vm: PlaceDetailsViewModel = hiltViewModel()
    val state = vm.state

    LaunchedEffect(placeId) { vm.load(placeId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.place?.name ?: "Подробнее") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                state.loading -> {
                    CircularProgressIndicator(Modifier.padding(24.dp))
                }
                state.error != null -> {
                    Text("Ошибка: ${state.error}", modifier = Modifier.padding(16.dp))
                }
                state.place != null -> {
                    val place = state.place!!

                    // фото уже приходят в порядке order_index (по твоим словам/бэку)
                    val photos = place.photos
                    val pagerState = rememberPagerState(pageCount = { photos.size.coerceAtLeast(1) })

                    Column(Modifier.fillMaxSize()) {
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

                        Column(Modifier.padding(16.dp)) {
                            Text(place.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))

                            place.averageRating?.let {
                                Text("Рейтинг: ${String.format("%.1f", it)}")
                                Spacer(Modifier.height(6.dp))
                            }

                            place.worktime?.takeIf { it.isNotBlank() }?.let {
                                Text("Часы работы: $it")
                                Spacer(Modifier.height(6.dp))
                            }

                            place.address?.takeIf { it.isNotBlank() }?.let {
                                Text("Адрес: $it")
                                Spacer(Modifier.height(6.dp))
                            }

                            place.description?.takeIf { it.isNotBlank() }?.let {
                                Spacer(Modifier.height(8.dp))
                                Text(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun toAbsoluteUrl(baseUrl: String, pathOrUrl: String): String {
    if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) return pathOrUrl
    val baseFixed = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
    val pathFixed = if (pathOrUrl.startsWith("/")) pathOrUrl else "/$pathOrUrl"
    return baseFixed + pathFixed
}
