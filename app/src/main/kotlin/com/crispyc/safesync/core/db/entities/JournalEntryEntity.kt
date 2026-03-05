package com.crispyc.safesync.core.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val timestamp: Long,
    val sentimentScore: Float, // 0.0 - 1.0
    val moodTag: String,
    val isEncrypted: Boolean = true
)
