package com.kavachsecurecomm.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * Manages the WebSocket connection for real-time secure messaging.
 */
class WebSocketManager {

    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()

    // Listener for incoming decrypted messages
    var onMessageReceived: ((String) -> Unit)? = null

    fun connect(serverUrl: String, jwtToken: String) {
        val request = Request.Builder()
            .url(serverUrl)
            // Send JWT Token in headers for initial handshake authentication
            .addHeader("Authorization", "Bearer $jwtToken")
            .build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                Log.d("WebSocket", "Connected securely to Kavach Node")
            }

            override fun onMessage(text: String) {
                // Here we receive the ENCRYPTED payload from the Server
                // E.g., JSON { "senderId": "uuid", "cipherText": "...", "iv": "..." }
                // The Decryption engine would intercept this.
                Log.d("WebSocket", "Received encrypted message.")
                onMessageReceived?.invoke(text)
            }

            override fun onMessage(bytes: ByteString) {
                Log.d("WebSocket", "Received binary message.")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closing: $reason")
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e("WebSocket", "Connection Failed: ${t.message}")
            }
        })
    }

    /**
     * Sends the fully encrypted payload text to the blind server relay.
     */
    fun sendEncryptedPayload(payloadJson: String) {
        webSocket?.send(payloadJson)
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }
}
