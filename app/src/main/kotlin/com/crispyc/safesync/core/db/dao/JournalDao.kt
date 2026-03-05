package com.crispyc.safesync.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.crispyc.safesync.core.db.entities.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<JournalEntryEntity>>

    @Insert
    suspend fun insertEntry(entry: JournalEntryEntity)

    @Query("SELECT * FROM journal_entries WHERE timestamp > :startTime")
    suspend fun getRecentEntries(startTime: Long): List<JournalEntryEntity>
}
