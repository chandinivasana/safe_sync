package com.crispyc.safesync.core.crypto

import android.content.Context
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.PublicKeySign
import com.google.crypto.tink.PublicKeyVerify
import com.google.crypto.tink.signature.SignatureConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var keysetHandle: KeysetHandle? = null

    init {
        SignatureConfig.register()
        generateKeys()
    }

    private fun generateKeys() {
        // In a real app, this should be stored securely (e.g., Android Keystore)
        keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("ED25519"))
    }

    fun sign(data: ByteArray): ByteArray {
        val signer = keysetHandle?.getPrimitive(PublicKeySign::class.java)
        return signer?.sign(data) ?: ByteArray(0)
    }

    fun verify(signature: ByteArray, data: ByteArray): Boolean {
        return try {
            val verifier = keysetHandle?.publicKeysetHandle?.getPrimitive(PublicKeyVerify::class.java)
            verifier?.verify(signature, data)
            true
        } catch (e: Exception) {
            false
        }
    }
}
