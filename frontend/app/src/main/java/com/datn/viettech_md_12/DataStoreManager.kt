package com.datn.viettech_md_12

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "search_history")

class DataStoreManager(private val context: Context) {

    private object PreferencesKeys {
        val SEARCH_HISTORY = stringSetPreferencesKey("search_history")
    }

    suspend fun saveSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            val currentHistory = preferences[PreferencesKeys.SEARCH_HISTORY] ?: emptySet()
            val updatedHistory = listOf(query) + currentHistory.filterNot { it == query }
            preferences[PreferencesKeys.SEARCH_HISTORY] = updatedHistory.take(10).toSet()
        }
    }

    fun getSearchHistory(): Flow<List<String>> {
        return context.dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.SEARCH_HISTORY]?.toList() ?: emptyList()
            }
    }

    suspend fun clearSearchHistory() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SEARCH_HISTORY] = emptySet()
        }
    }
}
