package com.crispyc.safesync.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crispyc.safesync.core.db.entities.UserProfileEntity
import com.crispyc.safesync.core.db.entities.SafetyEventEntity
import com.crispyc.safesync.core.db.entities.MeshMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)
}

@Dao
interface SafetyEventDao {
    @Query("SELECT * FROM safety_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<SafetyEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: SafetyEventEntity)

    @Query("SELECT * FROM safety_events WHERE isSyncedToBlockchain = 0")
    suspend fun getUnsyncedEvents(): List<SafetyEventEntity>

    @Query("UPDATE safety_events SET isSyncedToBlockchain = 1 WHERE eventHash IN (:hashes)")
    suspend fun markAsSynced(hashes: List<String>)
}

@Dao
interface MeshMessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: MeshMessageEntity): Long

    @Query("SELECT * FROM mesh_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MeshMessageEntity>>
}
