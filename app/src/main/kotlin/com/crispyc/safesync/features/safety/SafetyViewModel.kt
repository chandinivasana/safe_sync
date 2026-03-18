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
    private val cryptoManager: com.crispyc.safesync.core.crypto.CryptoManager,
    private val profileManager: com.crispyc.safesync.core.profile.ProfileManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isSosActive = MutableStateFlow(false)
    val isSosActive = _isSosActive.asStateFlow()

    private val _incomingSosAlert = MutableStateFlow<MeshPacketProtocol.MeshPacket?>(null)
    val incomingSosAlert = _incomingSosAlert.asStateFlow()

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

        // Listen for incoming mesh packets
        viewModelScope.launch {
            bleMeshManager.incomingPackets.collect { packet ->
                if (packet.type == MeshPacketProtocol.PacketType.SOS) {
                    // Verify signature and show alert (C-04)
                    if (cryptoManager.verify(packet.signature, packet.serialize())) {
                        _incomingSosAlert.value = packet
                    }
                }
            }
        }
    }

    fun dismissAlert() {
        _incomingSosAlert.value = null
    }

    fun toggleArView() {
        _isArViewActive.value = !_isArViewActive.value
    }

    fun setHomeZone(lat: Double, lng: Double) {
        viewModelScope.launch {
            profileManager.saveHomeZone(lat, lng)
        }
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
        // 1. Create SOS Packet
        val tempPacket = MeshPacketProtocol.MeshPacket(
            packetId = UUID.randomUUID().toString(),
            senderId = "user_hash",
            timestamp = System.currentTimeMillis(),
            ttl = 10,
            type = MeshPacketProtocol.PacketType.SOS,
            payload = "SOS: Lat: 0.0, Lng: 0.0".toByteArray(),
            signature = ByteArray(0)
        )
        
        // 2. Sign Packet
        val signature = cryptoManager.sign(tempPacket.serialize())
        val signedPacket = tempPacket.copy(signature = signature)
        
        // 3. Broadcast over Mesh
        bleMeshManager.broadcastPacket(signedPacket)

        // 4. Log Locally
        viewModelScope.launch {
            safetyEventDao.insertEvent(
                SafetyEventEntity(
                    eventHash = signedPacket.packetId,
                    timestamp = signedPacket.timestamp,
                    lat = 0.0,
                    lng = 0.0,
                    eventType = 1
                )
            )
        }

        // 5. Auto-dial contacts (PRD F1.1.4)
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
