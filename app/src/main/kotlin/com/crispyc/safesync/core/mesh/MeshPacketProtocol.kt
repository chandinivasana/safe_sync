package com.crispyc.safesync.core.mesh

import java.util.UUID

object MeshPacketProtocol {
    val SERVICE_UUID: UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")

    enum class PacketType(val id: Int) {
        SOS(0), BUDDY_BEACON(1), ANOMALY_WARN(2), GIG_POST(3),
        COWORK_SPOT(4), CHAT_MSG(5), PAYMENT_QR(6), MOOD_ANON(7)
    }

    data class MeshPacket(
        val packetId: String,
        val senderId: String,
        val timestamp: Long,
        var ttl: Int,
        val type: PacketType,
        val payload: ByteArray,
        val signature: ByteArray
    ) {
        fun serialize(): ByteArray = payload
        companion object {
            fun deserialize(data: ByteArray): MeshPacket = MeshPacket(
                UUID.randomUUID().toString(), "unknown", System.currentTimeMillis(),
                10, PacketType.BUDDY_BEACON, data, ByteArray(0)
            )
        }
    }
}
