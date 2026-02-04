package com.sfedu.degree_android.core.datastore


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_storage")

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY_ACCESS = stringPreferencesKey("access_token")

    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_ACCESS] }

    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { prefs -> prefs[KEY_ACCESS] = token }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs -> prefs.remove(KEY_ACCESS) }
    }
}
