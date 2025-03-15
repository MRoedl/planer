package com.example.planer.ViewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppDataStore private constructor(private val context: Context) {

    // Erstelle eine DataStore-Instanz
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")


    // Schlüssel für die Einstellungen
    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val DAYS_TO_PLAN = intPreferencesKey("days_to_plan")

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: AppDataStore? = null

        fun getInstance(context: Context): AppDataStore {
            return instance ?: synchronized(this) {
                instance ?: AppDataStore(context).also { instance = it }
            }
        }
    }

    // Dark Mode setzen
    suspend fun setDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { settings ->
            settings[DARK_MODE] = isDarkMode
        }
    }

    // Dark Mode abrufen
    fun isDarkMode(): Flow<Boolean> {
        return context.dataStore.data.map { settings ->
            settings[DARK_MODE] ?: false
        }
    }

    // DaysToPlan setzen
    suspend fun setDaysToPlan(daysToPlan: Int) {
        context.dataStore.edit { settings ->
            settings[DAYS_TO_PLAN] = daysToPlan
        }
    }

    // DaysToPlan abrufen
    fun getDaysToPlan(): Flow<Int?> {
        return context.dataStore.data.map { settings ->
            settings[DAYS_TO_PLAN]
        }
    }
}