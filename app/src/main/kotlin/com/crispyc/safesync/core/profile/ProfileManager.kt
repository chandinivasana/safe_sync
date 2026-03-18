package com.crispyc.safesync.core.profile

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_profile")

@Singleton
class ProfileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val NAME_KEY = stringPreferencesKey("user_name")
    private val LANGUAGE_KEY = stringPreferencesKey("user_lang")
    private val HOME_LAT_KEY = stringPreferencesKey("home_lat")
    private val HOME_LNG_KEY = stringPreferencesKey("home_lng")

    val homeZone: Flow<Pair<Double, Double>?> = context.dataStore.data.map { preferences ->
        val lat = preferences[HOME_LAT_KEY]?.toDoubleOrNull()
        val lng = preferences[HOME_LNG_KEY]?.toDoubleOrNull()
        if (lat != null && lng != null) lat to lng else null
    }

    suspend fun saveProfile(name: String, language: String) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = name
            preferences[LANGUAGE_KEY] = language
        }
    }

    suspend fun saveHomeZone(lat: Double, lng: Double) {
        context.dataStore.edit { preferences ->
            preferences[HOME_LAT_KEY] = lat.toString()
            preferences[HOME_LNG_KEY] = lng.toString()
        }
    }
}
