const db = require('../db');
const axios = require('axios');

/**
 * Handles incoming WebSocket connections and acts as a blind relay for encrypted packets.
 */
module.exports = (io) => {
    // Basic in-memory map of user_id to socket_id for fast online routing.
    // In production, use Redis.
    const activeConnections = new Map();

    io.on('connection', (socket) => {
        // Dummy user extraction. In reality, the socket handshake must contain a valid JWT.
        const userId = socket.handshake.query.userId;
        if (userId) {
            activeConnections.set(userId, socket.id);
            console.log(`User ${userId} securely connected on socket ${socket.id}`);
        }

        /**
         * The Server blind-forwards encrypted messages to the recipient 
         * or saves them offline.
         */
        socket.on('send_message', async (messagePacket) => {
            const { senderId, receiverId, groupId, encryptedPayload } = messagePacket;

            try {
                // Determine if routing to a single user or group
                if (receiverId) {
                    await sendDirectMessage(messagePacket);
                } else if (groupId) {
                    await sendGroupMessage(messagePacket);
                }

                // AI Security Module Check (Fire and forget to Python FastAPI container)
                axios.post(process.env.AI_SERVICE_URL + '/api/v1/analyze-traffic', { 
                    sender_id: senderId, 
                    receiver_id: receiverId, 
                    payload_size_bytes: encryptedPayload.length,
                    timestamp: Date.now() / 1000.0
                }).then(response => {
                    if (response.data.status === 'anomaly_detected') {
                        console.warn(`[SECURITY ALERT] Anomaly detected for user ${senderId}: ${response.data.reason}. Score: ${response.data.risk_score}`);
                    }
                }).catch(err => console.error("AI service connection error:", err.message));
                
            } catch (err) {
                console.error("Message forwarding error:", err);
            }
        });

        /**
         * High-priority emergency broadcast (Officers Only)
         */
        socket.on('emergency_broadcast', async (broadcastData) => {
             const { senderId, targetUnitId, encryptedPayload } = broadcastData;
             console.log(`!!! EMERGENCY BROADCAST from ${senderId} to Unit ${targetUnitId} !!!`);
             
             // In a real app, query DB for all members of the Unit, and emit the packet to them
             // with a special "priority: high" flag.
             io.emit('emergency_alert', { senderId, targetUnitId, encryptedPayload, priority: 'critical' });
        });

        socket.on('disconnect', () => {
             if (userId) activeConnections.delete(userId);
             console.log(`User disconnected: ${socket.id}`);
        });
    });

    async function sendDirectMessage(packet) {
         const { senderId, receiverId, encryptedPayload } = packet;

         // 1. Store the message blindly in DB (Using PostgreSQL Row-Level Security later)
         const result = await db.query(
             `INSERT INTO messages (sender_id, receiver_id, encrypted_payload) 
              VALUES ($1, $2, $3) RETURNING id`,
             [senderId, receiverId, encryptedPayload]
         );

         // 2. Check if receiver is online
         const recipientSocketId = activeConnections.get(receiverId);
         if (recipientSocketId) {
             io.to(recipientSocketId).emit('receive_message', {
                 messageId: result.rows[0].id,
                 senderId,
                 encryptedPayload
             });
         }
    }

    async function sendGroupMessage(packet) {
         // Logic to retrieve group members and route or store offline
         // Not fully implemented in MVP for brevity
    }
};
