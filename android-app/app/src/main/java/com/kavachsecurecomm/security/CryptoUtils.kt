package com.kavachsecurecomm.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Utility for AES-GCM End-to-End Encryption.
 * For MVP/Demo, we use a fixed key (In production, this is managed via Double Ratchet per-session keys).
 */
object CryptoUtils {
    private const val AES_MODE = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val IV_LENGTH = 12
    
    // DEMO ONLY: Hardcoded key for "trained" dataset testing.
    // In production, keys are exchanged via X3DH.
    private val DEMO_KEY = "KavachSecureCommDemoKey1234567890".take(32).toByteArray()
    private val secretKey = SecretKeySpec(DEMO_KEY, "AES")

    fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(AES_MODE)
        val iv = ByteArray(IV_LENGTH)
        SecureRandom().nextBytes(iv)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        val cipherText = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        
        val combined = ByteArray(iv.size + cipherText.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)
        
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decrypt(encodedCipherText: String): String {
        return try {
            val combined = Base64.decode(encodedCipherText, Base64.NO_WRAP)
            
            val iv = combined.copyOfRange(0, IV_LENGTH)
            val cipherText = combined.copyOfRange(IV_LENGTH, combined.size)
            
            val cipher = Cipher.getInstance(AES_MODE)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            String(cipher.doFinal(cipherText), Charsets.UTF_8)
        } catch (e: Exception) {
            "Error: Decryption Failed"
        }
    }
}
