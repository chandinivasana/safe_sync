package com.crispyc.safesync.core.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val mnemonicBackup: String?,
    val skills: String,
    val isSetupComplete: Boolean
)

@Entity(tableName = "safety_events")
data class SafetyEventEntity(
    @PrimaryKey val eventHash: String,
    val timestamp: Long,
    val lat: Double,
    val lng: Double,
    val eventType: Int,
    val isSyncedToBlockchain: Boolean = false
)

@Entity(tableName = "mesh_messages")
data class MeshMessageEntity(
    @PrimaryKey val messageId: String,
    val senderId: String,
    val timestamp: Long,
    val payload: ByteArray,
    val packetType: Int,
    val ttl: Int
)
