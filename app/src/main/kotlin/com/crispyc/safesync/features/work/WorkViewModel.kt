package com.crispyc.safesync.features.work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crispyc.safesync.core.mesh.BleMeshManager
import com.crispyc.safesync.core.mesh.MeshPacketProtocol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class Gig(
    val id: String,
    val title: String,
    val budget: String,
    val matchScore: Float
)

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val bleMeshManager: BleMeshManager
) : ViewModel() {

    private val _gigs = MutableStateFlow<List<Gig>>(emptyList())
    val gigs = _gigs.asStateFlow()

    init {
        viewModelScope.launch {
            bleMeshManager.incomingPackets.collect { packet ->
                if (packet.type == MeshPacketProtocol.PacketType.GIG_POST) {
                    processIncomingGig(packet)
                }
            }
        }
    }

    private fun processIncomingGig(packet: MeshPacketProtocol.MeshPacket) {
        // Mock matching logic (F2.2)
        val score = 0.85f 
        val newGig = Gig(
            id = packet.packetId,
            title = "Freelance Kotlin Dev Needed", // Decoded from payload
            budget = "₹5000",
            matchScore = score
        )
        _gigs.value = _gigs.value + newGig
    }

    fun postGig(title: String, budget: String) {
        val packet = MeshPacketProtocol.MeshPacket(
            packetId = UUID.randomUUID().toString(),
            senderId = "user_123",
            timestamp = System.currentTimeMillis(),
            ttl = 5,
            type = MeshPacketProtocol.PacketType.GIG_POST,
            payload = "$title|$budget".toByteArray(),
            signature = ByteArray(0)
        )
        bleMeshManager.broadcastPacket(packet)
    }
}
