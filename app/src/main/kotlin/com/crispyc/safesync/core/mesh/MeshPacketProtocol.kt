package com.crispyc.safesync.core.mesh

import java.nio.ByteBuffer
import java.util.UUID

object MeshPacketProtocol {
    val SERVICE_UUID: UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")

    enum class PacketType(val id: Int) {
        SOS(0), BUDDY_BEACON(1), ANOMALY_WARN(2), GIG_POST(3),
        COWORK_SPOT(4), CHAT_MSG(5), PAYMENT_QR(6), MOOD_ANON(7);

        companion object {
            fun fromId(id: Int): PacketType = entries.find { it.id == id } ?: BUDDY_BEACON
        }
    }

    data class MeshPacket(
        val packetId: String, // 36 chars
        val senderId: String, // Hashed 32 bytes hex
        val timestamp: Long,
        var ttl: Int,
        val type: PacketType,
        val payload: ByteArray,
        val signature: ByteArray // 64 bytes (Ed25519)
    ) {
        fun serialize(): ByteArray {
            val senderBytes = senderId.toByteArray().take(32).toByteArray()
            val buffer = ByteBuffer.allocate(1 + 1 + 8 + 32 + 4 + payload.size + signature.size)
            buffer.put(type.id.toByte())
            buffer.put(ttl.toByte())
            buffer.putLong(timestamp)
            
            val paddedSender = ByteArray(32)
            System.arraycopy(senderBytes, 0, paddedSender, 0, senderBytes.size)
            buffer.put(paddedSender)
            
            buffer.putInt(payload.size)
            buffer.put(payload)
            buffer.put(signature)
            return buffer.array()
        }

        companion object {
            fun deserialize(data: ByteArray): MeshPacket {
                val buffer = ByteBuffer.wrap(data)
                val typeId = buffer.get().toInt()
                val ttl = buffer.get().toInt()
                val timestamp = buffer.getLong()
                
                val senderBytes = ByteArray(32)
                buffer.get(senderBytes)
                val senderId = senderBytes.decodeToString().trim()
                
                val payloadSize = buffer.getInt()
                val payload = ByteArray(payloadSize)
                buffer.get(payload)
                
                val signature = ByteArray(64)
                if (buffer.remaining() >= 64) {
                    buffer.get(signature)
                }

                return MeshPacket(
                    packetId = UUID.randomUUID().toString(), // Should ideally be part of serialized data
                    senderId = senderId,
                    timestamp = timestamp,
                    ttl = ttl,
                    type = PacketType.fromId(typeId),
                    payload = payload,
                    signature = signature
                )
            }
        }
    }
}
