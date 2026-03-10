package com.crispyc.safesync.features.safety

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crispyc.safesync.core.db.dao.SafetyEventDao
import com.crispyc.safesync.core.db.entities.SafetyEventEntity
import com.crispyc.safesync.core.mesh.BleMeshManager
import com.crispyc.safesync.core.mesh.MeshPacketProtocol
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SafetyViewModel @Inject constructor(
    private val safetyManager: SafetyManager,
    private val bleMeshManager: BleMeshManager,
    private val safetyEventDao: SafetyEventDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isSosActive = MutableStateFlow(false)
    val isSosActive = _isSosActive.asStateFlow()

    private val _isArViewActive = MutableStateFlow(false)
    val isArViewActive = _isArViewActive.asStateFlow()

    private val _countdown = MutableStateFlow(10)
    val countdown = _countdown.asStateFlow()

    init {
        viewModelScope.launch {
            safetyManager.sosTriggered.collect {
                startSosWorkflow()
            }
        }
    }

    fun toggleArView() {
        _isArViewActive.value = !_isArViewActive.value
    }

    private fun startSosWorkflow() {
        _isSosActive.value = true
        _countdown.value = 10
        
        viewModelScope.launch {
            for (i in 10 downTo 1) {
                if (!_isSosActive.value) break
                _countdown.value = i
                delay(1000)
            }
            
            if (_isSosActive.value) {
                executeFinalSos()
            }
        }
    }

    fun cancelSos() {
        _isSosActive.value = false
    }

    private fun executeFinalSos() {
        // 1. Broadcast SOS over Mesh
        val sosPacket = MeshPacketProtocol.MeshPacket(
            packetId = UUID.randomUUID().toString(),
            senderId = "user_hash", // Should be actual user hash
            timestamp = System.currentTimeMillis(),
            ttl = 10,
            type = MeshPacketProtocol.PacketType.SOS,
            payload = "SOS: Lat: 0.0, Lng: 0.0".toByteArray(), // Need real GPS
            signature = ByteArray(0)
        )
        bleMeshManager.broadcastPacket(sosPacket)

        // 2. Log Locally
        viewModelScope.launch {
            safetyEventDao.insertEvent(
                SafetyEventEntity(
                    eventHash = sosPacket.packetId,
                    timestamp = sosPacket.timestamp,
                    lat = 0.0, // Need real GPS
                    lng = 0.0,
                    eventType = 1
                )
            )
        }

        // 3. Auto-dial contacts (PRD F1.1.4)
        // This is a placeholder for actual sequence dialing logic
        autoDialEmergencyContact()
    }

    private fun autoDialEmergencyContact() {
        // Implementation for F1.1.4 - sequentially dialing top 5 contacts
        // For MVP, dialing the first one
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:911") // Placeholder
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
}
