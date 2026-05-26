package com.sfedu.degree_android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sfedu.degree_android.core.network.ApiConstants
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.ui.screens.place.toAbsoluteUrl
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.sfedu.degree_android.core.util.formatWorktime

@Composable
fun ZonePlaceMiniCard(
    place: PlaceDto,
    isFavorite: Boolean,
    favoriteEnabled: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
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
            val rawPath = place.photos.firstOrNull { it.isMain }?.url ?: place.photos.firstOrNull()?.url
            val photoUrl = rawPath?.let { toAbsoluteUrl(ApiConstants.BASE_URL, it) }

            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(92.dp)
            ) {
                if (!photoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photoUrl)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .networkCachePolicy(CachePolicy.ENABLED)
                            .build(),
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

                    IconButton(
                        onClick = onFavoriteClick,
                        enabled = favoriteEnabled
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Избранное",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else LocalContentColor.current
                        )
                    }
                }

                place.category.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                }

                place.address?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(4.dp))
                    InfoLine(
                        icon = Icons.Filled.LocationOn,
                        text = it
                    )
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

