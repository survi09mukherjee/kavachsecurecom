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

    fun connect(serverUrl: String, jwtToken: String, userId: String) {
        // Append userId to URL for identification on the raw WebSocket backend
        val fullUrl = if (serverUrl.contains("?")) "$serverUrl&userId=$userId" else "$serverUrl?userId=$userId"
        
        val request = Request.Builder()
            .url(fullUrl)
            // Send JWT Token in headers for initial handshake authentication
            .addHeader("Authorization", "Bearer $jwtToken")
            .build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                Log.d("WebSocket", "Connected securely to Kavach Node as $userId")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received raw text: $text")
                try {
                    // Raw WebSockets receive a JSON string from our new backend
                    // Expecting: { "type": "receive_message", "senderId": "...", "encryptedPayload": "..." }
                    val json = org.json.JSONObject(text)
                    val type = json.optString("type")
                    
                    if (type == "receive_message" || type == "emergency_alert") {
                        val payload = json.optString("encryptedPayload")
                        onMessageReceived?.invoke(payload)
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error parsing message: ${e.message}")
                    // Fallback: search for a raw encrypted string if it's not valid JSON
                    onMessageReceived?.invoke(text)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
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
