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

    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[NAME_KEY]
    }

    suspend fun saveProfile(name: String, language: String) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = name
            preferences[LANGUAGE_KEY] = language
        }
    }
}
