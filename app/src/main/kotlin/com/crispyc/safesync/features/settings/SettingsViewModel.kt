package com.crispyc.safesync.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crispyc.safesync.core.db.SafeSyncDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val database: SafeSyncDatabase
) : ViewModel() {

    fun deleteAllLocalData() {
        viewModelScope.launch {
            database.clearAllTables()
        }
    }
}
