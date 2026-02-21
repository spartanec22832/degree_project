package com.sfedu.degree_android.ui.screens.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sfedu.degree_android.data.remote.dto.PlaceSearchDto

@Composable
fun MapSearchUi(
    expanded: Boolean,
    query: String,
    loading: Boolean,
    results: List<PlaceSearchDto>,
    error: String?,
    onToggle: () -> Unit,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    Box(Modifier.fillMaxSize()) {

        // Кнопка лупы справа сверху
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            IconButton(onClick = onToggle) {
                Icon(Icons.Default.Search, contentDescription = "Поиск")
            }
        }

        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(12.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = query,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            placeholder = { Text("Введите название объекта") },
                            trailingIcon = {
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = { onQueryChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Очистить")
                                    }
                                }
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Закрыть")
                        }
                    }

                    if (loading) {
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(error)
                    }

                    if (!loading && query.trim().length >= 2 && results.isEmpty() && error == null) {
                        Spacer(Modifier.height(8.dp))
                        Text("Ничего не найдено")
                    }

                    if (results.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Column {
                            results.forEach { item ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onItemClick(item.id) }
                                        .padding(vertical = 10.dp, horizontal = 8.dp)
                                ) {
                                    Text(item.name)
                                    Text(
                                        text = "${item.category} • ${item.type}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}