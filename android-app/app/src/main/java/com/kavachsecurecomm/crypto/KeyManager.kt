package com.kavachsecurecomm.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.UUID

/**
 * Manages the Cryptographic Identity of the User using Hardware-Backed Android Keystore.
 * In a Zero-Trust environment, the Private Key NEVER leaves the TEE/Secure Enclave.
 */
class KeyManager {

    private val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private val IDENTITY_ALIAS = "kavach_identity_key"

    init {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)
    }

    /**
     * Generates an EC KeyPair (Curve25519) for signing and authentication.
     * Returns the Public Key as a Base64 string to be sent to the backend.
     */
    fun generateIdentityKeyPair(): String {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)

        if (!keyStore.containsAlias(IDENTITY_ALIAS)) {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC, KEYSTORE_PROVIDER
            )

            val parameterSpec = KeyGenParameterSpec.Builder(
                IDENTITY_ALIAS,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            // Require user authentication (Biometrics/PIN) before this key can be used
            .setUserAuthenticationRequired(true) 
            .build()

            keyPairGenerator.initialize(parameterSpec)
            keyPairGenerator.generateKeyPair()
        }

        // Retrieve public key
        val entry = keyStore.getEntry(IDENTITY_ALIAS, null) as KeyStore.PrivateKeyEntry
        val publicKeyBytes = entry.certificate.publicKey.encoded
        
        return android.util.Base64.encodeToString(publicKeyBytes, android.util.Base64.NO_WRAP)
    }

    /**
     * Generates a unique Device ID for the installation.
     */
    fun getOrCreateDeviceId(): String {
        // Mock implementation. In real app, store in EncryptedSharedPreferences.
        return UUID.randomUUID().toString()
    }
}
