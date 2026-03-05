package com.crispyc.safesync.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.crispyc.safesync.core.db.dao.MeshMessageDao
import com.crispyc.safesync.core.db.dao.SafetyEventDao
import com.crispyc.safesync.core.db.dao.UserProfileDao
import com.crispyc.safesync.core.db.dao.JournalDao
import com.crispyc.safesync.core.db.entities.MeshMessageEntity
import com.crispyc.safesync.core.db.entities.SafetyEventEntity
import com.crispyc.safesync.core.db.entities.UserProfileEntity
import com.crispyc.safesync.core.db.entities.JournalEntryEntity

@Database(
    entities = [
        UserProfileEntity::class,
        SafetyEventEntity::class,
        MeshMessageEntity::class,
        JournalEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SafeSyncDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun safetyEventDao(): SafetyEventDao
    abstract fun meshMessageDao(): MeshMessageDao
    abstract fun journalDao(): JournalDao
}
