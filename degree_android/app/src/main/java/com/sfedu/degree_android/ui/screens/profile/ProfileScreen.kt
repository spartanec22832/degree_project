package com.sfedu.degree_android.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    vm: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { vm.load() }

    val state = vm.state
    val context = LocalContext.current

    var showChangePassword by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Профиль",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when {
                state.loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text("Загрузка...", style = MaterialTheme.typography.bodyMedium)
                }

                state.error != null -> {
                    Text(
                        text = state.error ?: "Ошибка",
                        color = MaterialTheme.colorScheme.error
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { vm.load() }) { Text("Повторить") }
                    }
                }

                state.user != null -> {
                    val user = state.user!!

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProfileRow(label = "Логин", value = user.username)
                            ProfileRow(label = "Дата регистрации", value = formatCreatedAt(user.createdAt))
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Кнопки снизу в один ряд
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showChangePassword = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Сменить пароль")
                        }

                        Button(
                            onClick = { vm.logout(onLogout) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Выйти")
                        }
                    }
                }
            }
        }
    }

    if (showChangePassword) {
        ChangePasswordDialog(
            loading = vm.changePasswordLoading,
            onDismiss = { showChangePassword = false },
            onSubmit = { current, newPass, confirm ->
                // минимальная клиентская валидация (бэкенд все равно валидирует тоже)
                val err = validatePasswordChange(current, newPass, confirm)
                if (err != null) {
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                    return@ChangePasswordDialog
                }

                vm.changePassword(
                    currentPassword = current,
                    newPassword = newPass,
                    confirmationPassword = confirm,
                    onDone = {
                        Toast.makeText(context, "Пароль изменён. Войдите заново", Toast.LENGTH_SHORT).show()
                        vm.logout(onLogout)
                        showChangePassword = false
                    },
                    onError = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
}

@Composable
private fun ProfileRow(label: String, value: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value?.takeIf { it.isNotBlank() } ?: "—",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatCreatedAt(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "—"
    // ожидаем ISO, берем только YYYY-MM-DD
    val datePart = createdAt.take(10)
    return if (datePart.length == 10 && datePart[4] == '-' && datePart[7] == '-') {
        val y = datePart.substring(0, 4)
        val m = datePart.substring(5, 7)
        val d = datePart.substring(8, 10)
        "$d.$m.$y"
    } else createdAt
}

private fun validatePasswordChange(current: String, newPass: String, confirm: String): String? {
    if (current.isBlank()) return "Введите текущий пароль"
    if (newPass.isBlank()) return "Введите новый пароль"
    if (newPass.length < 6) return "Новый пароль должен быть минимум 6 символов"
    if (confirm.isBlank()) return "Подтвердите пароль"
    if (newPass != confirm) return "Пароли не совпадают"
    if (current == newPass) return "Новый пароль не должен совпадать с текущим"
    return null
}

@Composable
private fun ChangePasswordDialog(
    loading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (current: String, newPass: String, confirm: String) -> Unit
) {
    var current by rememberSaveable { mutableStateOf("") }
    var newPass by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }

    val currentError = current.isBlank()
    val newError = newPass.isBlank() || newPass.length < 6
    val confirmError = confirm.isBlank() || confirm != newPass

    val canSubmit = !loading && !currentError && !newError && !confirmError

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text("Смена пароля") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = current,
                    onValueChange = { current = it },
                    enabled = !loading,
                    label = { Text("Текущий пароль") },
                    singleLine = true,
                    isError = currentError,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    enabled = !loading,
                    label = { Text("Новый пароль") },
                    singleLine = true,
                    isError = newError,
                    visualTransformation = PasswordVisualTransformation(),
                    supportingText = {
                        if (newError) Text(
                            "Минимум 6 символов",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    enabled = !loading,
                    label = { Text("Подтвердите пароль") },
                    singleLine = true,
                    isError = confirmError,
                    visualTransformation = PasswordVisualTransformation(),
                    supportingText = {
                        if (confirmError) Text(
                            "Пароли не совпадают",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (loading) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.width(18.dp))
                        Text("Сохраняем...")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSubmit,
                onClick = { onSubmit(current, newPass, confirm) }
            ) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(
                enabled = !loading,
                onClick = onDismiss
            ) { Text("Отмена") }
        }
    )
}
