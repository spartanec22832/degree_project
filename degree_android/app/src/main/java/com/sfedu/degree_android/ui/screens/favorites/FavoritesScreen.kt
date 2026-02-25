package com.sfedu.degree_android.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sfedu.degree_android.core.network.ApiConstants
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.ui.screens.place.toAbsoluteUrl
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector
import com.sfedu.degree_android.core.util.*


@Composable
fun FavoritesScreen(
    onOpenDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val favStateVm: FavoritesStateViewModel = hiltViewModel()
    val vm: FavoritesViewModel = hiltViewModel()

    // Перезагружаем список при любом изменении избранных
    val favoriteIds by favStateVm.favoriteIds.collectAsState()

    LaunchedEffect(Unit) {
        favStateVm.refresh()
    }

    LaunchedEffect(favoriteIds) {
        vm.load(favoriteIds)
    }

    val state = vm.state

    when {
        state.loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка: ${state.error}")
            }
        }

        state.items.isEmpty() -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("В избранном пока пусто")
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.items, key = { it.id }) { place ->
                    FavoriteMiniCard(
                        place = place,
                        onClick = { onOpenDetails(place.id) },
                        onRemove = { favStateVm.toggle(place.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteMiniCard(
    place: PlaceDto,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 2.dp,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Левая картинка: сначала main, иначе первая
            val rawPath = place.photos.firstOrNull { it.isMain }?.url ?: place.photos.firstOrNull()?.url
            val photoUrl = rawPath?.let { toAbsoluteUrl(ApiConstants.BASE_URL, it) }

            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(92.dp)
            ) {
                if (!photoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize())
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                // Рейтинг + сердечко (удаление) в одной строке
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val avg = place.averageRating ?: 0.0
                        Text(
                            text = String.format("%.1f", avg),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Удалить из избранного",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // category (по твоему DTO это строка)
                place.category.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                }

                place.address?.takeIf { it.isNotBlank() }?.let {
                    InfoLine(
                        icon = Icons.Filled.LocationOn,
                        text = it
                    )
                    Spacer(Modifier.height(4.dp))
                }

                formatWorktime(place.worktime)?.let { formatted ->
                    Spacer(Modifier.height(4.dp))
                    InfoLine(
                        icon = Icons.Filled.AccessTime,
                        text = formatted
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoLine(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
