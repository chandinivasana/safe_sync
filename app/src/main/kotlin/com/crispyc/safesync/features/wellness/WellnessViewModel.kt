package com.crispyc.safesync.features.wellness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crispyc.safesync.core.ai.AiManager
import com.crispyc.safesync.core.db.dao.JournalDao
import com.crispyc.safesync.core.db.entities.JournalEntryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WellnessViewModel @Inject constructor(
    private val journalDao: JournalDao,
    private val aiManager: AiManager
) : ViewModel() {

    val journalEntries = journalDao.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addJournalEntry(content: String) {
        viewModelScope.launch {
            // In a real app, we'd run sentiment analysis here using AiManager
            val sentiment = 0.8f // Mock
            
            val entry = JournalEntryEntity(
                content = content,
                timestamp = System.currentTimeMillis(),
                sentimentScore = sentiment,
                moodTag = if (sentiment > 0.5) "Happy" else "Stressed"
            )
            journalDao.insertEntry(entry)
        }
    }
    
    fun performMoodSync() {
        // Collect multimodal data (mocked) and calculate score
        val features = FloatArray(12) { 0.5f }
        val score = aiManager.calculateMoodScore(features)
        // Handle score...
    }
}
