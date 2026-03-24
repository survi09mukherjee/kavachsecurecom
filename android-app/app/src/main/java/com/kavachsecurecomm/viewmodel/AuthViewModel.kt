package com.kavachsecurecomm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.kavachsecurecomm.crypto.KeyManager
import com.kavachsecurecomm.network.AuthRepository

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val keyManager: KeyManager = KeyManager()
) : ViewModel() {

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    /**
     * Completes the zero-trust onboarding:
     * 1. Validates invite token with backend.
     * 2. If valid, generates a local Ed25519 matching Identity Key.
     * 3. Registers device + public key tying it to the token.
     */
    fun validateAndRegister(inviteToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            try {
                // Step 1: Validate Token (mocked API call)
                val isValid = authRepository.validateToken(inviteToken)
                
                if (!isValid) {
                    _authState.value = AuthState.Error("Invalid or expired token.")
                    return@launch
                }

                // Step 2: Generate Cryptographic Identity in Android Keystore
                val publicIdentityKey = keyManager.generateIdentityKeyPair()
                val deviceId = keyManager.getOrCreateDeviceId()
                
                // In production, user would authenticate via Keycloak IAM here
                // We use a placeholder keycloakId "kc_user_123" for demo
                val keycloakId = "kc_user_123" 

                // Step 3: Register Device and Key with the server
                val registered = authRepository.registerDevice(
                    token = inviteToken,
                    publicIdentityKey = publicIdentityKey,
                    deviceId = deviceId,
                    keycloakId = keycloakId
                )

                if (registered) {
                    _authState.value = AuthState.Success
                    onSuccess()
                } else {
                    _authState.value = AuthState.Error("Device registration failed.")
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown security error occurred.")
            }
        }
    }
}
