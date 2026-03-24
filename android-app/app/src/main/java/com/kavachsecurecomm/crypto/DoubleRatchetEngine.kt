package com.kavachsecurecomm.crypto

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Simulates the Signal Protocol (Double Ratchet Algorithm).
 * Real implementation would depend on org.whispersystems.signal-protocol-java
 * For this MVP architecture, we demonstrate AES-256-GCM authenticated encryption.
 */
class DoubleRatchetEngine {

    companion object {
        private const val ALGORITHM = "AES/GCM/NoPadding"
        private const val TAG_LENGTH_BIT = 128
        private const val IV_LENGTH_BYTE = 12
    }

    /**
     * Encrypts plaintext message using a shared secret derived from X3DH
     */
    fun encryptMessage(plainText: String, sharedSecretKey: SecretKey): EncryptedMessage {
        val cipher = Cipher.getInstance(ALGORITHM)
        
        // Generate random IV for each message
        val iv = ByteArray(IV_LENGTH_BYTE)
        SecureRandom().nextBytes(iv)
        
        val parameterSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
        cipher.init(Cipher.ENCRYPT_MODE, sharedSecretKey, parameterSpec)
        
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        
        return EncryptedMessage(
            cipherTextBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP),
            ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
        )
    }

    /**
     * Decrypts ciphertext message
     */
    fun decryptMessage(encryptedMessage: EncryptedMessage, sharedSecretKey: SecretKey): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        
        val ivBytes = Base64.decode(encryptedMessage.ivBase64, Base64.NO_WRAP)
        val cipherTextBytes = Base64.decode(encryptedMessage.cipherTextBase64, Base64.NO_WRAP)
        
        val parameterSpec = GCMParameterSpec(TAG_LENGTH_BIT, ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, sharedSecretKey, parameterSpec)
        
        val decryptedBytes = cipher.doFinal(cipherTextBytes)
        
        return String(decryptedBytes, Charsets.UTF_8)
    }

    /**
     * Simulate Key Derivation for a shared secret using simulated X3DH handshake
     */
    fun deriveSharedSecretMock(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }
}

data class EncryptedMessage(
    val cipherTextBase64: String,
    val ivBase64: String
)
